package com.insadelyon.les24heures.service;

import com.insadelyon.les24heures.DTO.DayResourceDTO;
import com.insadelyon.les24heures.DTO.NightResourceDTO;
import com.insadelyon.les24heures.model.Category;
import com.insadelyon.les24heures.model.DayResource;
import com.insadelyon.les24heures.model.NightResource;
import com.insadelyon.les24heures.utils.Day;

import java.util.ArrayList;

/**
 * Created by remi on 27/12/14.
 */
public interface ResourceService {

    public DayResource fromDTO(DayResourceDTO dayResourceDTO,ArrayList<Category> categories);

    public ArrayList<DayResource> fromDTO(ArrayList<DayResourceDTO> dayResourceDTOs, ArrayList<Category> categories);

    public NightResource fromDTO(NightResourceDTO nightResourceDTO);

    public ArrayList<NightResource> fromDTO(ArrayList<NightResourceDTO> nightResourceDTOs);

    public ArrayList<NightResource> filterByDay(ArrayList<NightResource> nightResources, Day day);


    public ArrayList<DayResource> getDayResources(ArrayList<DayResource> dayResources);

    ArrayList<DayResource> getFacilities(ArrayList<DayResource> newDayResources);
}
