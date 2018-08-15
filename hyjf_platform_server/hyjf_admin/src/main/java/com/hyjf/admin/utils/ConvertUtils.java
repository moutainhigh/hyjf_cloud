package com.hyjf.admin.utils;

import com.hyjf.admin.beans.vo.DropDownVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther:yangchangwei
 * @Date:2018/7/4
 * @Description: 参数转换工具类
 */
public  class ConvertUtils {
    public static final Logger logger = LoggerFactory.getLogger(ConvertUtils.class);
    /**
     * 将MAP参数转换成实体类
     * @param mapParam  map参数，key值要和实体类的属性一致
     * @param T  转换后的class类
     * @return  转换后的带内容的实体类
     */
    public static Object convertMapToObject(Map<String, Object> mapParam, Class T){
        Object obj = null;
        try {
            obj = T.newInstance();
            Field[] fs = T.getDeclaredFields();
            for(int i = 0 ; i < fs.length; i++){
                Field f = fs[i];
                f.setAccessible(true); //设置些属性是可以访问的
                String mtdName = "set"+ f.getName().substring(0,1).toUpperCase() + f.getName().substring(1);
                Method method = T.getMethod(mtdName, f.getType());
                method.invoke(obj,mapParam.get(f.getName()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 将实体类转换成Map
     * @param bean 需要转换的实体类
     * @return  转换后的带内容的MAp key为实体类的属性名
     */
    public static Map convertObjectToMap(Object bean){
        Map<String, Object> mapParam = new HashMap<String, Object>();
        try {
            Class beanClass = bean.getClass();
            Field[] fs = beanClass.getDeclaredFields();
            for(int i = 0 ; i < fs.length; i++){
                Field f = fs[i];
                f.setAccessible(true); //设置些属性是可以访问的
                mapParam.put(f.getName(),f.get(bean));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mapParam;
    }

    /**
     * paramMap转下拉
     * @param paramMap
     * @return
     */
    public static List<DropDownVO> convertParamMapToDropDown(Map<String, String> paramMap){
        if(!CollectionUtils.isEmpty(paramMap)){
            try {
                List<DropDownVO> dropDownVOList = new ArrayList<DropDownVO>();
                paramMap.forEach((key, value) -> dropDownVOList.add(new DropDownVO(key, value)));
                return dropDownVOList;
            } catch (Exception e){
                logger.error("map转换下拉列表时发生异常：", e);
                return null;
            }
        }
        return null;
    }

    /**
     * list转下拉
     * @param list
     * @param keyItem
     * @param valueItem
     * @param <S>
     * @return
     */
    public static <S> List<DropDownVO> convertListToDropDown(List<S> list, String keyItem, String valueItem){
        if(!CollectionUtils.isEmpty(list)){
            try {
                List<DropDownVO> dropDownVOList = new ArrayList<DropDownVO>();
                for(int i = 0;i < list.size(); i++){
                    DropDownVO dropDownVO = setDropDown(list.get(i), keyItem, valueItem);
                    dropDownVOList.add(dropDownVO);
                }
                return dropDownVOList;
            } catch (Exception e){
                logger.error("list转换下拉列表时发生异常：", e);
                return null;
            }
        }
        return null;
    }

    private static <S> DropDownVO setDropDown(S bean, String keyItem, String valueItem) throws Exception{
        DropDownVO dropDownVO = new DropDownVO();
        Field[] field = bean.getClass().getDeclaredFields();
        for (int i = 0; i < field.length; i++){
            field[i].setAccessible(true);
            if(field[i].getName().equals(keyItem) || field[i].getName().equals(valueItem)){
                String itemName = field[i].getName();
                itemName = itemName.replaceFirst(itemName.substring(0, 1),itemName.substring(0, 1).toUpperCase());
                Method method = bean.getClass().getMethod("get" + itemName);
                if(field[i].getName().equals(keyItem)){
                    dropDownVO.setKey(method.invoke(bean).toString());
                } else {
                    dropDownVO.setValue(method.invoke(bean).toString());
                }
            }
        }
        return dropDownVO;
    }
}
