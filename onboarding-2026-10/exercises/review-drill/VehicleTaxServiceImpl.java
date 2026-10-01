package com.example.vehicletax.domain.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.vehicletax.domain.model.Owner;
import com.example.vehicletax.domain.model.TaxRecord;
import com.example.vehicletax.domain.model.Vehicle;
import com.example.vehicletax.domain.repository.TaxRecordRepository;
import com.example.vehicletax.domain.repository.VehicleRepository;

// レビュー演習用。コンパイルは通らなくてよい（読むための教材）。
// 指摘は「行番号・何が問題か・どう直すか・重要度（高/中/低）」の形で書く。
@Service
public class VehicleTaxServiceImpl implements VehicleTaxService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleTaxServiceImpl.class);

    @Inject
    VehicleRepository vehicleRepository;

    @Inject
    TaxRecordRepository taxRecordRepository;

    @Override
    public double calcTax(String vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId).get();
        double base = vehicle.getBaseAmount();
        if (vehicle.getType() == "ECO") {
            return base * 0.75;
        }
        return base;
    }

    @Override
    public String ownerLabel(String vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
        Owner owner = vehicle.getOwner();
        logger.info("owner={}, plate={}, address={}", owner.getName(), vehicle.getPlateNumber(), owner.getAddress());
        return owner.getName().trim() + " 様";
    }

    @Override
    public void registerPayments(List<TaxRecord> records) {
        for (int i = 0; i <= records.size(); i++) {
            savePayment(records.get(i));
        }
    }

    @Transactional
    private void savePayment(TaxRecord record) {
        try {
            taxRecordRepository.insert(record);
            vehicleRepository.updatePaid(record.getVehicleId());
        } catch (Exception e) {
        }
    }

    @Override
    public List<TaxRecord> importCsv(String path) throws IOException {
        List<TaxRecord> result = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] cols = line.split(",");
            result.add(new TaxRecord(cols[0], Integer.valueOf(cols[1])));
        }
        return result;
    }

    @Override
    public boolean isSameAmount(TaxRecord a, TaxRecord b) {
        return a.getAmount() == b.getAmount();
    }

    @Override
    public List<Vehicle> search(String prefecture) {
        return vehicleRepository.findByPrefecture(prefecture);
    }
}
