
import com.telq.sdk.model.tests.v3.lnt.LntApiTlvDto;
import com.telq.sdk.model.tests.v3.lnt.LntApiUdhDto;
import com.telq.sdk.model.tests.v3.lnt.RouteAttribute;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class LntApiSupplierDto {

    private Long supplierId;

    private String supplierName;

    private String routeType;

    private List<RouteAttribute> attributeList;

    private String comment;

    private String serviceType;

    private List<LntApiTlvDto> tlvs;

    private List<LntApiUdhDto> udhs;

    private Long smppSessionId;

    private Long userId;
}
