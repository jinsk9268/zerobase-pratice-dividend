package com.zerobase.dividend.service;

import com.zerobase.dividend.model.Company;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor // Bean이 생성될 때 사용할 수 있도록
public class CompanyService {
    public Company save(String ticker) {
        return null;
    }

    private Company storeCompanyAndDividend(String ticker) {
        return null;
    }
}
