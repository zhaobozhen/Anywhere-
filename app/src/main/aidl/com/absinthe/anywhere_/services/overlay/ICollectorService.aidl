// ICollectorService.aidl
package com.absinthe.anywhere_.services.overlay;

// Declare any non-default types here with import statements

interface ICollectorService {
    void startCollector();
    void stopCollector();
    void startCoordinator();
    void stopCoordinator(in int x, in int y);
}
