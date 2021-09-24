/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo.lo;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * This class keeps devices ids without LO prefix. In fact it keeps node id.
 */
@Component
public class LoDeviceCache {

    private KeySetView<String, Boolean> cache = ConcurrentHashMap.newKeySet();

    public void add(String deviceId) {
        cache.add(deviceId);
    }

    public void addAll(Collection<? extends String> c) {
    	cache.addAll(c);
    }

    public void delete(String deviceId) {
        cache.remove(deviceId);
    }

    public boolean contains(String deviceId) {
        return cache.contains(deviceId);
    }
    
    public Collection<String> getAll() {
    	return cache.stream().collect(Collectors.toSet());
    }
}