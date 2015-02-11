package com.insalyon.les24heures.service.impl;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.insalyon.les24heures.DTO.AssomakerDTO;
import com.insalyon.les24heures.DTO.ResourceDTO;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.model.Schedule;
import com.insalyon.les24heures.service.ResourceRetrofitService;
import com.insalyon.les24heures.service.ResourceService;
import com.insalyon.les24heures.utils.Day;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by remi on 27/12/14.
 */
public class ResourceServiceImpl implements ResourceService  {

    private static ResourceServiceImpl resourceService;
    private static CategoryServiceImpl categoryService;
    private static ScheduleServiceImpl scheduleService;


    private EventBus eventBus;

    private ResourceServiceImpl(){
        eventBus = EventBus.getDefault();

    }

    public static ResourceServiceImpl getInstance(){
        if(resourceService == null){
            //synchronized (resourceService) {
                resourceService = new ResourceServiceImpl();
                categoryService = CategoryServiceImpl.getInstance();
                scheduleService = ScheduleServiceImpl.getInstance();
            //}
        }
        return resourceService;
    }

    public Resource fromDTO(ResourceDTO resourceDTO){
        //TODO doit venir du backend
        Random rand = new Random();
        Category category = categoryService.getCategories().get(rand.nextInt(categoryService.getCategories().size()));
        Boolean isFavorites = (rand.nextInt(2) == 0 ? true : false);


        return new Resource(resourceDTO.getNom(),resourceDTO.getDescription(),
                scheduleService.fromDTO(resourceDTO.getHoraires()),
                new LatLng(Double.valueOf(resourceDTO.getLocX()),Double.valueOf(resourceDTO.getLocY())), category,isFavorites);
    }

    public ArrayList<Resource> fromDTO(ArrayList<ResourceDTO> resourceDTOs) {
        ArrayList<Resource>  resources = new ArrayList<>();

        for (ResourceDTO resourceDTO : resourceDTOs) {
            resources.add(this.fromDTO(resourceDTO));
        }

        return resources;

    }


    public void getResourcesAsyncFromBackend(ResourceRetrofitService resourceRetrofitService){
        resourceRetrofitService.getResources(new Callback<AssomakerDTO>() {
            @Override
            public void success(AssomakerDTO assomakerDTO, Response response) {

                Log.d("getResources", "sucess");

                ArrayList<ResourceDTO> resourceDTOs = new ArrayList<ResourceDTO>();

                Map<Integer, ArrayList<ResourceDTO>> animations = assomakerDTO.getAnimations();
                for (ArrayList<ResourceDTO> dtos : animations.values()) {
                    resourceDTOs.addAll(dtos);
                }
                Log.d("getResources",resourceDTOs.toString());

                ResourcesUpdatedEvent resourcesUpdatedEvent = new ResourcesUpdatedEvent(fromDTO(resourceDTOs));
                eventBus.post(resourcesUpdatedEvent);
            }


            @Override
            public void failure(RetrofitError error) {

                Log.d("getResources", "failure " + error);

            }
        });

    }

    public void getResourcesAsyncMock(){
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] params) {
                Log.d("mock","");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ArrayList<Resource> resourcesList = new ArrayList<>();

                List<Schedule> schedules = new ArrayList<Schedule>();
                schedules.add(new Schedule(Day.MONDAY,new Date(),new Date()));
                resourcesList.add(new Resource("se divertir", "les plaisirs c'est bien pour les calins et les chateau coconuts", schedules, new LatLng(45.783088762965, 4.8747852427139), categoryService.getCategories().get(0)));
                resourcesList.add(new Resource("se cultiver", "la culture on s'en fout sauf Alexis et Jeaaane", schedules, new LatLng(45.783514302374, 4.8747852427139), categoryService.getCategories().get(1)));
                resourcesList.add(new Resource("du sport", "du sport pour les pédales et éliminer l'apero parce qu'il ne faut pas déconner", schedules, new LatLng(45.784196093864, 4.8747852427139), categoryService.getCategories().get(2)));
                resourcesList.add(new Resource("mes favoris", "mes favoris pour bien montrer que j'ai des gouts de merde", schedules, new LatLng(45.783827609484, 4.8747852427139), categoryService.getCategories().get(2)));
                resourcesList.add(new Resource("lieux utiles", "où qu'on boit où qu'on pisse, où qu'on mange", schedules, new LatLng(45.784196093888, 4.8747852427139), categoryService.getCategories().get(0)));

                ResourcesUpdatedEvent resourcesUpdatedEvent = new ResourcesUpdatedEvent(resourcesList);
                eventBus.post(resourcesUpdatedEvent);
                return null;
            }
        }.execute();

    }

    @Override
    public Schedule getNextSchedule(Resource resource) {
        //TODO according to current time
        return resource.getSchedules().get(0);
    }

}
