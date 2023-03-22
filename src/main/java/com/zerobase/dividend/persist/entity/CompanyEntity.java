package com.zerobase.dividend.persist.entity;

import com.zerobase.dividend.model.Company;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity(name = "company")
@Getter
@ToString
@NoArgsConstructor
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true) // ticker 중복되면 안되므로 unique 설정
    private String ticker;
    private String name;

    // DB 저장을 쉽게 하기 위한 생성자 생성
    public CompanyEntity(Company company) {
        this.ticker = company.getTicker();
        this.name = company.getName();
    }
}
