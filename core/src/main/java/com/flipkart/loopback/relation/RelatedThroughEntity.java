package com.flipkart.loopback.relation;

import com.flipkart.loopback.model.PersistedModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by akshaya.sharma on 12/04/18
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RelatedThroughEntity {
  PersistedModel throughEntity;
  PersistedModel toEntity;
}
