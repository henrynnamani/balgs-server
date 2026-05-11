package com.graey.Balgs.model;

import com.graey.Balgs.common.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "ratings")
public class Rating extends BaseEntity {
    @ManyToOne
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Max(5)
    @Min(0)
    private int rating;

    private String review;
}
