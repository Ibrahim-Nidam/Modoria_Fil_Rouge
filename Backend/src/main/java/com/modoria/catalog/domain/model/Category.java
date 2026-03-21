package com.modoria.catalog.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories", indexes = {
    @Index(name = "idx_category_name", columnList = "name"),
    @Index(name = "idx_category_season", columnList = "season")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Season season;

    @Column(length = 500)
    private String description;

    @Column(length = 1000)
    private String imagePath;

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;
}
