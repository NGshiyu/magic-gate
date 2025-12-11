package com.magicgate.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** @Author yangyangSheep @Description 对象拷贝工具类 @CreateTime 2024/4/2 18:25 */
public class BeanCopyUtils {

  /** 拷贝的时候忽略空字符串和空值的方法 */
  public static String[] getNullAndBlankPropertyNames(Object source) {
    final BeanWrapper src = new BeanWrapperImpl(source);
    java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
    List<String> collect =
        Arrays.stream(pds)
            .map(FeatureDescriptor::getName)
            .filter(
                    name -> {
                  boolean flag = false;
                  // check if value of this property is null then add it to the collection
                  Object srcValue = src.getPropertyValue(name);
                  if (srcValue == null) {
                    // 特定字符写在此处过滤，收集不需要copy的字段列表。此处过滤null为例
                    flag = true;
                  }
                  if (srcValue instanceof String && StringUtils.isBlank((CharSequence) srcValue)) {
                    flag = true;
                  }
                  return flag;
                })
            .toList();

    String[] result = new String[collect.size()];
    return collect.toArray(result);
  }
}
