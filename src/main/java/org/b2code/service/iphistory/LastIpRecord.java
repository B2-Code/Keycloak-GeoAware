package org.b2code.service.iphistory;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LastIpRecord {
    private String ip;
    private Long time;
}
