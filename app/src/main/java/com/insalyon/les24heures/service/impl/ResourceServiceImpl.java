package com.insalyon.les24heures.service.impl;

import com.google.android.gms.maps.model.LatLng;
import com.insalyon.les24heures.DTO.DayResourceDTO;
import com.insalyon.les24heures.DTO.NightResourceDTO;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.NightResource;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.model.Schedule;
import com.insalyon.les24heures.service.ResourceService;

import java.util.ArrayList;
import java.util.Random;

import de.greenrobot.event.EventBus;

/**
 * Created by remi on 27/12/14.
 */
public class ResourceServiceImpl implements ResourceService {
    private static final String TAG = ResourceServiceImpl.class.getCanonicalName();

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

    @Override
    public DayResource fromDTO(DayResourceDTO dayResourceDTO,ArrayList<Category> categories) {
        Random rand = new Random();
        Category category = categoryService.findById(categories,dayResourceDTO.getCategory());

        //just pour le test
        Boolean isFavorites = (rand.nextInt(2) == 0 ? true : false);

        return new DayResource(dayResourceDTO.getName(),
                dayResourceDTO.getDescription(),
                scheduleService.fromDTO(dayResourceDTO.getSchedule()),
                category,
                dayResourceDTO.getMain_picture_url(),
                dayResourceDTO.getPictures(),
                new LatLng(Double.valueOf(dayResourceDTO.getLocalisation().get(0)),
                        Double.valueOf(dayResourceDTO.getLocalisation().get(1))));
    }

    @Override
    public NightResource fromDTO(NightResourceDTO nightResourceDTO) {
        Random rand = new Random();
        //just pour le test
        Boolean isFavorites = (rand.nextInt(2) == 0 ? true : false);


        return new NightResource(nightResourceDTO.getName(),
                nightResourceDTO.getDescription(),
                scheduleService.fromDTO(nightResourceDTO.getSchedule()),
                null,//nightResourceDTO.getCategory(),
                isFavorites,
                nightResourceDTO.getFacebook_url(),
                nightResourceDTO.getTwitter_url(),
                nightResourceDTO.getSite_url(),
                nightResourceDTO.getStage());
    }




    @Override
    public ArrayList<DayResource> fromDTO(ArrayList<DayResourceDTO> dayResourceDTOs, ArrayList<Category> categories) {
        ArrayList<DayResource> dayResources = new ArrayList<>();

        for (DayResourceDTO dayResourceDTO : dayResourceDTOs) {
            dayResources.add(this.fromDTO(dayResourceDTO,categories));
        }

        return dayResources;

    }

    @Override
    public ArrayList<NightResource> fromDTO(ArrayList<NightResourceDTO> nightResourceDTOs) {
        ArrayList<NightResource> nightResources = new ArrayList<>();

        for (NightResourceDTO nightResourceDTO : nightResourceDTOs) {
            nightResources.add(this.fromDTO(nightResourceDTO));
        }

        return nightResources;

    }



    @Override
    public Schedule getNextSchedule(Resource dayResource) {
        //TODO according to current time
        return dayResource.getSchedules().get(0);
    }

}
