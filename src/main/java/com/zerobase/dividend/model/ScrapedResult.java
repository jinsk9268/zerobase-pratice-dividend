package com.zerobase.dividend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 스크래핑한 결과를 주고받기 위한 result class
 * 스크래핑한 회사의 정보를 가진 Company Instance
 * 배당금 정보를 가진 Dividend List
 * 한 회사는 여러가지 배당금 정보를 가지기 때문
 */
@Data
@AllArgsConstructor // 모든 필드를 초기화하는 생성자 코드를 사용할 수 있는 어노테이션
public class ScrapedResult {
    private Company company;
    private List<Dividend> dividends;

    public ScrapedResult() {
        this.dividends = new ArrayList<>();
    }
}
