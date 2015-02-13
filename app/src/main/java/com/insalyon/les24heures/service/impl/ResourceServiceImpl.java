package com.insalyon.les24heures.service.impl;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.insalyon.les24heures.DTO.AssomakerDTO;
import com.insalyon.les24heures.DTO.DayResourceDTO;
import com.insalyon.les24heures.DTO.NightResourceDTO;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.NightResource;
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
public class ResourceServiceImpl implements ResourceService {

    private static ResourceServiceImpl resourceService;
    private static CategoryServiceImpl categoryService;
    private static ScheduleServiceImpl scheduleService;


    private EventBus eventBus;

    private ResourceServiceImpl() {
        eventBus = EventBus.getDefault();

    }

    public static ResourceServiceImpl getInstance() {
        if (resourceService == null) {
            //synchronized (resourceService) {
            resourceService = new ResourceServiceImpl();
            categoryService = CategoryServiceImpl.getInstance();
            scheduleService = ScheduleServiceImpl.getInstance();
            //}
        }
        return resourceService;
    }

    public DayResource fromDTO(DayResourceDTO dayResourceDTO) {
        //TODO doit venir du backend
        Random rand = new Random();
        Category category = categoryService.getCategories().get(rand.nextInt(categoryService.getCategories().size()));

        //just pour le test
        Boolean isFavorites = (rand.nextInt(2) == 0 ? true : false);


        return new DayResource(dayResourceDTO.getNom(),
                dayResourceDTO.getDescription(),
                scheduleService.fromDTO(dayResourceDTO.getHoraires()),
                new LatLng(Double.valueOf(dayResourceDTO.getLocX()),
                        Double.valueOf(dayResourceDTO.getLocY())),
                category,
                isFavorites);
    }

    public NightResource fromDTO(NightResourceDTO nightResourceDTO) {
        //TODO doit venir du backend
        Random rand = new Random();
        Category category = categoryService.getCategories().get(rand.nextInt(categoryService.getCategories().size()));
        Boolean isFavorites = (rand.nextInt(2) == 0 ? true : false);


        return new NightResource(nightResourceDTO.getName(),
                nightResourceDTO.getDescription(),
                scheduleService.fromDTO(nightResourceDTO.getHoraires()),
                category,//nightResourceDTO.getCategory(),
                isFavorites,
                nightResourceDTO.getFacebook_url(),
                nightResourceDTO.getTwitter_url(),
                nightResourceDTO.getSite_url(),
                nightResourceDTO.getStage());
    }


    public ArrayList<DayResource> fromDTO(ArrayList<DayResourceDTO> dayResourceDTOs) {
        ArrayList<DayResource> dayResources = new ArrayList<>();

        for (DayResourceDTO dayResourceDTO : dayResourceDTOs) {
            dayResources.add(this.fromDTO(dayResourceDTO));
        }

        return dayResources;

    }

    public ArrayList<NightResource> fromDTO(List<NightResourceDTO> nightResourceDTOs) {
        ArrayList<NightResource> nightResources = new ArrayList<>();

        for (NightResourceDTO nightResourceDTO : nightResourceDTOs) {
            nightResources.add(this.fromDTO(nightResourceDTO));
        }

        return nightResources;

    }


    public void getResourcesAsyncFromBackend(ResourceRetrofitService resourceRetrofitService) {
        resourceRetrofitService.getResources(new Callback<AssomakerDTO>() {
            @Override
            public void success(AssomakerDTO assomakerDTO, Response response) {

                Log.d("getResources", "sucess");

                ArrayList<DayResourceDTO> dayResourceDTOs = new ArrayList<DayResourceDTO>();

                Map<Integer, ArrayList<DayResourceDTO>> animations = assomakerDTO.getAnimations();
                for (ArrayList<DayResourceDTO> dtos : animations.values()) {
                    dayResourceDTOs.addAll(dtos);
                }
                Log.d("getResources", dayResourceDTOs.toString());

                ArrayList<NightResourceDTO> nightResourceDTOs = assomakerDTO.getArtists();

                //TODO bricolage temporaire pour le double backend
                ResourcesUpdatedEvent resourcesUpdatedEvent;
                if (nightResourceDTOs != null) {
                    resourcesUpdatedEvent = new ResourcesUpdatedEvent(fromDTO(dayResourceDTOs),
                            fromDTO(nightResourceDTOs));
                } else {
                    resourcesUpdatedEvent = new ResourcesUpdatedEvent(fromDTO(dayResourceDTOs));
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
                resourcesList.add(new DayResource("se divertir", "les plaisirs c'est bien pour les calins et les chateau coconuts", schedules, new LatLng(45.783088762965, 4.8747852427139), categoryService.getCategories().get(0)));
                resourcesList.add(new DayResource("se cultiver", "la culture on s'en fout sauf Alexis et Jeaaane", schedules, new LatLng(45.783514302374, 4.8747852427139), categoryService.getCategories().get(1)));
                resourcesList.add(new DayResource("du sport", "du sport pour les pédales et éliminer l'apero parce qu'il ne faut pas déconner", schedules, new LatLng(45.784196093864, 4.8747852427139), categoryService.getCategories().get(2)));
                resourcesList.add(new DayResource("mes favoris", "mes favoris pour bien montrer que j'ai des gouts de merde", schedules, new LatLng(45.783827609484, 4.8747852427139), categoryService.getCategories().get(2)));
                resourcesList.add(new DayResource("lieux utiles", "où qu'on boit où qu'on pisse, où qu'on mange", schedules, new LatLng(45.784196093888, 4.8747852427139), categoryService.getCategories().get(0)));

                ResourcesUpdatedEvent resourcesUpdatedEvent = new ResourcesUpdatedEvent(resourcesList);
                eventBus.post(resourcesUpdatedEvent);
                return null;
            }
        }.execute();

    }

    @Override
    public Schedule getNextSchedule(Resource dayResource) {
        //TODO according to current time
        return dayResource.getSchedules().get(0);
    }

}
