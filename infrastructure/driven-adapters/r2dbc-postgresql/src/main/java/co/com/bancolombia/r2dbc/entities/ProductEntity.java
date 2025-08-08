package co.com.bancolombia.r2dbc.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("products")
public class ProductEntity {
    @Id
    @Column("id")
    private Long productId;
    @Column("name")
    private String name;
    @Column("stock")
    private Integer stock;
    @Column("branch_id")
    private Long branchId;
}
