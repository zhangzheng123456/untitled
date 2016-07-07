package com.bizvane.ishop.service.imp;


import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.sun.app.client.Client;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.Status;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by zhou on 2016/7/6.
 *
 * @@version
 */

@Service
public class IceInterfaceServiceImpl implements IceInterfaceService {

    String[] arg = new String[]{"--Ice.Config=client.config"};
    Client client = new Client(arg);

    public DataBox iceInterface(String method ,Map datalist){
        DataBox dataBox1 = new DataBox("1", Status.ONGOING, "", method, datalist, null, null, System.currentTimeMillis());
        DataBox dataBox = client.put(dataBox1);

        return dataBox;
    }
}
