package org.elitclass.db.likes;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.elitclass.db.classes.ClassesEntity;
import org.elitclass.db.user.UserEntity;

@Table( name = "likes"
//        uniqueConstraints = {
//                @UniqueConstraint(
//                        name = "recommend_uk",
//                        columnNames = {"class_id", "user_id"}
//                )
//        }
)
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class LikesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "class_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ClassesEntity classes;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;
}
