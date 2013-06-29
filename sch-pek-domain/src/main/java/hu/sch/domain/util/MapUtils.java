package hu.sch.domain.util;

import hu.sch.domain.interfaces.HasUserRelation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author messo
 */
public class MapUtils {

    public static <T extends HasUserRelation> Map<Long, T> createMapWithUserIdKey(List<T> list) {
        Map<Long, T> map = new HashMap<Long, T>(list.size());
        for(T obj : list) {
            map.put(obj.getUserId(), obj);
        }
        return map;
    }

}
