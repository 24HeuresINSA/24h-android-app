package com.insalyon.les24heures.service;


import com.insalyon.les24heures.DTO.LiveUpdateDTO;
import com.insalyon.les24heures.model.LiveUpdate;

import java.util.ArrayList;
import java.util.List;

public interface LiveUpdateService {

    public void getLiveUpdatesAsyncFromBackend(RetrofitService retrofitService);

    public LiveUpdate fromDTO(LiveUpdateDTO liveUpdateDTO);

    public List<LiveUpdate> fromDTO(List<LiveUpdateDTO> liveUpdateDTOs);


}
