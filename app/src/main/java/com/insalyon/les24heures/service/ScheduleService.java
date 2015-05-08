package com.insalyon.les24heures.service;

import com.insalyon.les24heures.DTO.ScheduleDTO;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.model.Schedule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remi on 29/01/15.
 */
public interface ScheduleService {


    public Schedule fromDTO(ScheduleDTO scheduleDTO);

    String printSchedules(List<Schedule> schedules);

    String printNextSchedule(List<Schedule> schedules);

    public ArrayList<Schedule> fromDTO(ArrayList<ScheduleDTO> scheduleDTOs);

    public Schedule getNextSchedule(Resource dayResource);

    ArrayList<Schedule> getNextSchedules(Resource dayResource);

    public ArrayList<Schedule> getTodayNextSchedules(Resource dayResource);
}
