package co.com.bancolombia.r2dbc.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("franchise")
public class FranchiseEntity {

    @Id
    @Column("franchise_id")
    private Integer franchiseId;
    @Column("name")
    private String name;
}
