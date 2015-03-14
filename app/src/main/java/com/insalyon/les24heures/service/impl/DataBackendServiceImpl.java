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
import com.insalyon.les24heures.model.Schedule;
import com.insalyon.les24heures.service.DataBackendService;
import com.insalyon.les24heures.service.RetrofitService;
import com.insalyon.les24heures.utils.AlphabeticalSortComparator;
import com.insalyon.les24heures.utils.Day;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @Deprecated
    public void getResourcesAsyncFromBackend(RetrofitService retrofitService) {
        retrofitService.getResources(new Callback<AssomakerDTO>() {
            @Override
            public void success(AssomakerDTO assomakerDTO, Response response) {

                Log.d(TAG,"getResources success");

                //bricolage temporaire pour les category qui ne sont pas encore dans le backend
                ArrayList<Category> categories = new ArrayList<Category>();
                categories.add(new Category("CAT1","ic_CAT1"));
                categories.add(new Category("CAT2","ic_CAT2"));
                categories.add(new Category("CAT3","ic_CAT3"));
                categories.add(new Category("ALL","ic_ALLCATEGORY"));//WARNING ic-ALLCATEGORY is mandatory for the filter
                CategoriesUpdatedEvent categoriesUpdatedEvent = new CategoriesUpdatedEvent(categories);
                eventBus.post(categoriesUpdatedEvent);


                ArrayList<DayResourceDTO> dayResourceDTOs = new ArrayList<DayResourceDTO>();

                Map<Integer, ArrayList<DayResourceDTO>> animations = assomakerDTO.getAnimations();
                for (ArrayList<DayResourceDTO> dtos : animations.values()) {
                    dayResourceDTOs.addAll(dtos);
                }
                Log.d(TAG, "getResources "+ dayResourceDTOs.toString());

                ArrayList<NightResourceDTO> nightResourceDTOs = assomakerDTO.getArtists();

                //TODO bricolage temporaire pour le double backend
                ResourcesUpdatedEvent resourcesUpdatedEvent;
                if (nightResourceDTOs != null) {
                    resourcesUpdatedEvent = new ResourcesUpdatedEvent(resourceService.fromDTO(dayResourceDTOs,categories),
                            resourceService.fromDTO(nightResourceDTOs));
                    Collections.sort(resourcesUpdatedEvent.getDayResourceList(), new AlphabeticalSortComparator());
                    Collections.sort(resourcesUpdatedEvent.getNightResourceList(),new AlphabeticalSortComparator());
                } else {
                    resourcesUpdatedEvent = new ResourcesUpdatedEvent(resourceService.fromDTO(dayResourceDTOs,categories));
                    Collections.sort(resourcesUpdatedEvent.getDayResourceList(),new AlphabeticalSortComparator());
                    ArrayList<NightResource> mockArtist = new ArrayList<NightResource>();

                    List<Schedule> schedules = new ArrayList<Schedule>();
                    schedules.add(new Schedule(Day.MONDAY, new Date(), new Date()));

                    mockArtist.add(new NightResource("untitle", "blabla", schedules, new Category("pouet", "ic"), "fb", "tweet", "site", "BIG"));
                    mockArtist.add(new NightResource("deuxtitle", "blabla", schedules, new Category("pouet", "ic"), "fb", "tweet", "site", "BIG"));

                    resourcesUpdatedEvent.setNightResourceList(mockArtist);
                }


                eventBus.post(resourcesUpdatedEvent);
            }


            @Override
            public void failure(RetrofitError error) {

                Log.d("getResources", "failure " + error);

            }
        });

    }

    public void getResourcesAsyncFromBackend(RetrofitService retrofitService, final String dataVersion) {
        retrofitService.getResources(dataVersion,new Callback<AssomakerDTO>() {
            @Override
            public void success(AssomakerDTO assomakerDTO, Response response) {

                Log.d(TAG,"getResources : success");
                //TODO analyse response to know if we have to parse JSON to retrieve updated data


                String dataVersion = assomakerDTO.getVersion();
                ArrayList<CategoryDTO> categoryDTOs = assomakerDTO.getCategories();
                ArrayList<DayResourceDTO> dayResourceDTOs = assomakerDTO.getResources();
                ArrayList<NightResourceDTO> nightResourceDTOs = assomakerDTO.getArtists();


                ArrayList<Category> categories = categoryService.fromDTO(categoryDTOs);
                CategoriesUpdatedEvent categoriesUpdatedEvent = new CategoriesUpdatedEvent(categories,dataVersion);
                eventBus.post(categoriesUpdatedEvent);


                ResourcesUpdatedEvent resourcesUpdatedEvent = new ResourcesUpdatedEvent(resourceService.fromDTO(dayResourceDTOs,categories),
                        resourceService.fromDTO(nightResourceDTOs),dataVersion);
                Collections.sort(resourcesUpdatedEvent.getDayResourceList(),new AlphabeticalSortComparator());
                Collections.sort(resourcesUpdatedEvent.getNightResourceList(),new AlphabeticalSortComparator());
                eventBus.post(resourcesUpdatedEvent);
            }


            @Override
            public void failure(RetrofitError error) {

                Log.d("getResources", "failure " + error);

            }
        });

    }

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
