package com.smartpickup.client;

import com.smartpickup.client.gui.SmartPickupConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * ModMenu integration: exposes the Smart Pickup config screen from the
 * ModMenu mod list (the Fabric equivalent of the NeoForge "Config" button).
 */
public final class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SmartPickupConfigScreen::new;
    }
}