package org.elitclass.db.page;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.elitclass.db.BaseEntity;

@Table(name= "page")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class PageEntity extends BaseEntity {

    @Column(nullable = false)
    private String pageType;

    @Column(length = 50,nullable = false)
    private String title;

    @Column(nullable = false)
    private String context;

    @Column(nullable = false)
    private Long lectureId;
}
