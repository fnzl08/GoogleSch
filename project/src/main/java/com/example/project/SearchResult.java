package com.example.project;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Entity //해당 클래스가 jpa 엔티티. 데이터베이스의 테이블과 매핑될 수 있는 객체임을 의미
@Table(name = "Search_results")
//엔티티가 매핑될 테이블의 이름을 지정합니다. search_results라는 이름의 테이블과 매핑
public class SearchResult {

    @Id //엔티티의 pk를 나타낸다. 아래 어노테이션과 함께 사용
    @GeneratedValue(strategy = GenerationType.IDENTITY) //db의 identity 컬럼을 사용하여 pk값 생성
    private Long id;
    private String title;
    private String author;
    private LocalDate publishedData;
    private String publisher;
    private String link;
}
