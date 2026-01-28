package com.backend.core.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandFeatureDto {
  private String feature;
  private String platform;
  private String key;
  private String brand;
  private String country;
  private Object value;
}
