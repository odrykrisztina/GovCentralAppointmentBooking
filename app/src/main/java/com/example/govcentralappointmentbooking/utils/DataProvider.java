package com.example.govcentralappointmentbooking.utils;

import com.example.govcentralappointmentbooking.models.Office;
import com.example.govcentralappointmentbooking.models.Service;
import java.util.ArrayList;
import java.util.List;

public class DataProvider {

    public static List<Office> getOfficeList() {
        List<Office> offices = new ArrayList<>();
        offices.add(new Office("RAKO", "6722 Szeged, Rákóczi tér 1.", "+3662680049"));
        offices.add(new Office("KERE", "6728 Szeged, Kereskedő köz 5/A-B.", "+3662681640"));
        offices.add(new Office("SZOR", "6726 Szeged, Szőregi út 80.", "+3662680113"));
        return offices;
    }

    public static List<Service> getServiceList() {
        List<Service> services = new ArrayList<>();
        services.add(new Service("DIGA", "Állampolgárság"));
        services.add(new Service("GEPJ", "Gépjármű"));
        services.add(new Service("SZIG", "Igazolvány"));
        services.add(new Service("UTLE", "Úlevél"));
        services.add(new Service("UREG", "Ügyfélkapu"));
        services.add(new Service("VEZE", "Vezetői engedély"));
        services.add(new Service("EGYE", "Egyéb"));
        return services;
    }
}
