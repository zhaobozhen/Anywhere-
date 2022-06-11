// ICollectorService.aidl
package com.absinthe.anywhere_.services.overlay;
import com.absinthe.anywhere_.services.overlay.ICollectorListener;

// Declare any non-default types here with import statements

interface ICollectorService {
    void startCollector();
    void stopCollector();
    void startCoordinator();
    void stopCoordinator(in int x, in int y);
    String[] getCurrentActivity();
    void registerCollectorListener(in ICollectorListener listener);
    void unregisterCollectorListener(in ICollectorListener listener);
}
