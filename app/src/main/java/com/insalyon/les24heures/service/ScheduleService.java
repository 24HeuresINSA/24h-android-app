package com.insalyon.les24heures.service;

import com.insalyon.les24heures.DTO.ScheduleDTO;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.model.Schedule;

import java.util.ArrayList;

/**
 * Created by remi on 29/01/15.
 */
public interface ScheduleService {


    public Schedule fromDTO(ScheduleDTO scheduleDTO);

    public ArrayList<Schedule> fromDTO(ArrayList<ScheduleDTO> scheduleDTOs);

    public Schedule getNextSchedule(Resource dayResource);

    public ArrayList<Schedule> getNextSchedules(Resource dayResource);
}
