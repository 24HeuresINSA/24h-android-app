package com.insalyon.les24heures.service.impl;

import com.insalyon.les24heures.DTO.ScheduleDTO;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.model.Schedule;
import com.insalyon.les24heures.service.ScheduleService;
import com.insalyon.les24heures.utils.Day;
import com.insalyon.les24heures.utils.ScheduleComparator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by remi on 29/01/15.
 */
public class ScheduleServiceImpl implements ScheduleService {
    private static ScheduleServiceImpl scheduleService;

    public static ScheduleServiceImpl getInstance() {
        if (scheduleService == null) {
            //synchronized (resourceService) {
            scheduleService = new ScheduleServiceImpl();
            //}
        }
        return scheduleService;
    }

    @Override
    public Schedule fromDTO(ScheduleDTO scheduleDTO) {
        Day day;
        Date start = getHour(scheduleDTO.getDebut());
        Date end = getHour(scheduleDTO.getFin());
        switch (scheduleDTO.getJour()) {
            case "Samedi":
                day = Day.SATURDAY;
                break;
            case "Dimanche":
                day = Day.SUNDAY;
                break;
            case "Vendredi":
                day = Day.FRIDAY;
                break;
            default:
                day = Day.NODAY;
                break;
        }


        return new Schedule(day, start, end);
    }


    private Date getHour(String debut) {

        int i;
        if (debut.length() == 5)
            i = 0;
        else
            i = 1;

        return new Date(0, 0, 0,
                (int) Integer.valueOf(debut.substring(0, 2 - i)),
                (int) Integer.valueOf(debut.substring(3 - i, 5 - i)));
    }

    @Override
    public Schedule getNextSchedule(Resource dayResource) {
        return getNextSchedules(dayResource).get(0);
    }

    public ArrayList<Schedule> getNextSchedulesMocked(Resource dayResource, String dateFormat) {
        Date nowDate = null;
        try {
            nowDate = new SimpleDateFormat("hh:mm/dd/M/yyyy").parse("11:10/23/5/2015");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar now = Calendar.getInstance();
        now.setTime(nowDate);
        Day nowDay = Day.values()[(now.get(Calendar.DAY_OF_WEEK))-2]; //TODO pourquoi -2 ?!
        int nowHours = now.get(Calendar.HOUR_OF_DAY);

        ArrayList<Schedule> result = new ArrayList<>();
        for (Schedule schedule : dayResource.getSchedules()) {
            if(schedule.getDay().equals(nowDay)
                    && (schedule.getEnd().getHours() >= nowHours
                    || schedule.getEnd().getHours() == 0)) //traitement de faveur pour le minuit des 24h de cinema
                result.add(schedule);
        }


        return result;
    }

    @Override
    public ArrayList<Schedule> getNextSchedules(Resource dayResource) {
        Date nowDate = new Date();

        Calendar now = Calendar.getInstance();
        now.setTime(nowDate);
        Day nowDay = Day.values()[(now.get(Calendar.DAY_OF_WEEK))-2]; //TODO pourquoi -2 ?!
        int nowHours = now.get(Calendar.HOUR_OF_DAY);

        ArrayList<Schedule> result = new ArrayList<>();
        for (Schedule schedule : dayResource.getSchedules()) {
            if(schedule.getDay().equals(nowDay)
                    && (schedule.getEnd().getHours() >= nowHours
                        || schedule.getEnd().getHours() == 0)) //traitement de faveur pour le minuit des 24h de cinema
                result.add(schedule);
        }


        return result;
    }



    @Override
    public ArrayList<Schedule> fromDTO(ArrayList<ScheduleDTO> scheduleDTOs) {
        ArrayList<Schedule> schedules = new ArrayList<>();

        for (ScheduleDTO scheduleDTO : scheduleDTOs) {
            schedules.add(this.fromDTO(scheduleDTO));
        }

        Collections.sort(schedules, new ScheduleComparator());

        return schedules;
    }
}
