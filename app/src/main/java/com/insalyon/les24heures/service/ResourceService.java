package com.insalyon.les24heures.service;

import com.insalyon.les24heures.DTO.DayResourceDTO;
import com.insalyon.les24heures.DTO.NightResourceDTO;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.NightResource;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.model.Schedule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remi on 27/12/14.
 */
public interface ResourceService {

    public DayResource fromDTO(DayResourceDTO dayResourceDTO,ArrayList<Category> categories);

    public ArrayList<DayResource> fromDTO(ArrayList<DayResourceDTO> dayResourceDTOs, ArrayList<Category> categories);

    public NightResource fromDTO(NightResourceDTO nightResourceDTO);

    public ArrayList<NightResource> fromDTO(ArrayList<NightResourceDTO> nightResourceDTOs);


    public Schedule getNextSchedule(Resource dayResource);
}
