package com.insalyon.les24heures.service;

import com.insalyon.les24heures.DTO.ResourceDTO;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.model.Schedule;

import java.util.ArrayList;

/**
 * Created by remi on 27/12/14.
 */
public interface ResourceService {

    public Resource fromDTO(ResourceDTO resourceDTO);

    public ArrayList<Resource> fromDTO(ArrayList<ResourceDTO> resourceDTOs);

    public void getResourcesAsyncFromBackend(ResourceRetrofitService resourceRetrofitService);

    //TODO ce doit etre l'implentation de getResourcesAsyncFromBackend qui effectue le mock et l'IOC qui inject la bonne implem
    //TODO mettre en place le design pattern factory ou de l'IOC
    public void getResourcesAsyncMock();


    public Schedule getNextSchedule(Resource resource);
}
