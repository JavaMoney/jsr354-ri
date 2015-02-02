package org.javamoney.moneta.function;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.money.CurrencyUnit;

/**
 * This map is decorator of HashMap that returns an empty Summary when there
 * isn't currency in get's method
 *
 * @author otaviojava
 */
class MonetarySummaryMap implements
        Map<CurrencyUnit, MonetarySummaryStatistics> {

    private Map<CurrencyUnit, MonetarySummaryStatistics> map = new HashMap<>();

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public MonetarySummaryStatistics get(Object key) {
        if (CurrencyUnit.class.isInstance(key)) {
            CurrencyUnit unit = CurrencyUnit.class.cast(key);
            return map.getOrDefault(unit, new DefaultMonetarySummaryStatistics(
                    unit));
        }
        return map.get(key);
    }

    @Override
    public MonetarySummaryStatistics put(CurrencyUnit key,
                                         MonetarySummaryStatistics value) {
        return map.put(key, value);
    }

    @Override
    public MonetarySummaryStatistics remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(
            Map<? extends CurrencyUnit, ? extends MonetarySummaryStatistics> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<CurrencyUnit> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<MonetarySummaryStatistics> values() {
        return map.values();
    }

    @Override
    public Set<java.util.Map.Entry<CurrencyUnit, MonetarySummaryStatistics>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object obj) {
        if (MonetarySummaryMap.class.isInstance(obj)) {
            MonetarySummaryMap other = MonetarySummaryMap.class.cast(obj);
            return map.equals(other.map);
        }
        return false;
    }

    @Override
    public MonetarySummaryStatistics putIfAbsent(CurrencyUnit key,
                                                 MonetarySummaryStatistics value) {
        MonetarySummaryStatistics v = map.get(key);
        if (Objects.isNull(v)) {
            v = put(key, value);
        }
        return v;
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public String toString() {
        return "MonetarySummaryMap: " + map.toString();
    }
}
