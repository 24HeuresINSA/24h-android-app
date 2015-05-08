package com.insalyon.les24heures.service.impl;

import android.os.AsyncTask;
import android.util.Log;

import com.insalyon.les24heures.DTO.AssomakerDTO;
import com.insalyon.les24heures.DTO.CategoryDTO;
import com.insalyon.les24heures.DTO.DayResourceDTO;
import com.insalyon.les24heures.DTO.NightResourceDTO;
import com.insalyon.les24heures.eventbus.CategoriesUpdatedEvent;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.NightResource;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.model.Schedule;
import com.insalyon.les24heures.service.DataBackendService;
import com.insalyon.les24heures.service.RetrofitService;
import com.insalyon.les24heures.utils.AlphabeticalSortComparator;
import com.insalyon.les24heures.utils.Day;
import com.insalyon.les24heures.utils.PositionSortComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by remi on 14/03/15.
 */
public class DataBackendServiceImpl implements DataBackendService {
    private static final String TAG = DataBackendServiceImpl.class.getCanonicalName();


    private static DataBackendServiceImpl dataBackendService;
    private static ResourceServiceImpl resourceService;
    private static CategoryServiceImpl categoryService;
    private static ScheduleServiceImpl scheduleService;

    public static DataBackendServiceImpl getInstance() {
        if (dataBackendService == null) {
//            synchronized (categoryService) {
            dataBackendService = new DataBackendServiceImpl();
            resourceService = ResourceServiceImpl.getInstance();
            categoryService = CategoryServiceImpl.getInstance();
            scheduleService = ScheduleServiceImpl.getInstance();
//            }
        }
        return dataBackendService;
    }

    private EventBus eventBus;

    public DataBackendServiceImpl() {
        eventBus = EventBus.getDefault();
    }


    @Override
    public void getResourcesAsyncFromBackend(RetrofitService retrofitService, final String dataVersion, final ArrayList<DayResource> dayResources, final ArrayList<NightResource> nightResources) {
        retrofitService.getResources(dataVersion,new Callback<AssomakerDTO>() {
            @Override
            public void success(AssomakerDTO assomakerDTO, Response response) {


                if(response.getStatus() == 204){
                    Log.d(TAG,"getOriginalResources : success : data already up to date");
                    return;
                }

                String dataVersion = assomakerDTO.getVersion();
                ArrayList<CategoryDTO> categoryDTOs = assomakerDTO.getCategories();
                ArrayList<DayResourceDTO> dayResourceDTOs = assomakerDTO.getResources();
                ArrayList<NightResourceDTO> nightResourceDTOs = assomakerDTO.getArtists();

                Log.d(TAG,"getOriginalResources : "+categoryDTOs.size()+" categories, "+dayResourceDTOs.size()+" dayResources, "+nightResourceDTOs.size()+" nightResources");


                ArrayList<Category> categories = categoryService.fromDTO(categoryDTOs);
                CategoriesUpdatedEvent categoriesUpdatedEvent = new CategoriesUpdatedEvent(categories,dataVersion);
                eventBus.post(categoriesUpdatedEvent);


                ArrayList<DayResource> newDayResources = resourceService.fromDTO(dayResourceDTOs, categories);
                ArrayList<NightResource> newNightResources = resourceService.fromDTO(nightResourceDTOs);
                ArrayList<DayResource> newFacilities = resourceService.getFacilities(newDayResources);
                newDayResources = resourceService.getDayResources(newDayResources);


                Collections.sort(newDayResources,new AlphabeticalSortComparator());
                Collections.sort(newNightResources,new PositionSortComparator());

                if(dayResources != null) restoreFavorites(dayResources, newDayResources);
                if(nightResources != null)restoreFavorites(nightResources,newNightResources);

                ResourcesUpdatedEvent resourcesUpdatedEvent = new ResourcesUpdatedEvent(newDayResources,newNightResources, newFacilities,dataVersion);

                eventBus.post(resourcesUpdatedEvent);
            }


            @Override
            public void failure(RetrofitError error) {

                Log.d("getOriginalResources", "failure " + error);

            }
        });

    }

    private void restoreFavorites(ArrayList<? extends Resource> resources, ArrayList<? extends Resource> newResources) {
        Resource temp;
        for (Resource resource : resources) {
            if(resource.isFavorites()){
                temp = getResourceById(newResources, resource);
                if(temp != null)
                    temp.setIsFavorites(true);
            }
        }

    }

    private Resource getResourceById(ArrayList<? extends Resource> resources, Resource resource) {

        for (Resource res : resources) {
            if(res.get_id().equals(resource.get_id())) return res;
        }
        return null;
    }

    @Deprecated
    public void getResourcesAsyncMock() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Log.d("mock", "");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ArrayList<DayResource> resourcesList = new ArrayList<>();

                List<Schedule> schedules = new ArrayList<Schedule>();
                schedules.add(new Schedule(Day.MONDAY, new Date(), new Date()));
//                resourcesList.add(new DayResource("se divertir", "les plaisirs c'est bien pour les calins et les chateau coconuts", schedules, new LatLng(45.783088762965, 4.8747852427139), categoryService.getCategories().get(0)));
//                resourcesList.add(new DayResource("se cultiver", "la culture on s'en fout sauf Alexis et Jeaaane", schedules, new LatLng(45.783514302374, 4.8747852427139), categoryService.getCategories().get(1)));
//                resourcesList.add(new DayResource("du sport", "du sport pour les pédales et éliminer l'apero parce qu'il ne faut pas déconner", schedules, new LatLng(45.784196093864, 4.8747852427139), categoryService.getCategories().get(2)));
//                resourcesList.add(new DayResource("mes favoris", "mes favoris pour bien montrer que j'ai des gouts de merde", schedules, new LatLng(45.783827609484, 4.8747852427139), categoryService.getCategories().get(2)));
//                resourcesList.add(new DayResource("lieux utiles", "où qu'on boit où qu'on pisse, où qu'on mange", schedules, new LatLng(45.784196093888, 4.8747852427139), categoryService.getCategories().get(0)));

                ResourcesUpdatedEvent resourcesUpdatedEvent = new ResourcesUpdatedEvent(resourcesList);
                eventBus.post(resourcesUpdatedEvent);
                return null;
            }
        }.execute();

    }
}
