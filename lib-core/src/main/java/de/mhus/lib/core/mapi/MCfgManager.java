/**
 * Copyright 2018 Mike Hummel
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.core.mapi;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import de.mhus.lib.annotations.activator.DefaultFactory;
import de.mhus.lib.core.MActivator;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MConstants;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.cfg.CfgInitiator;
import de.mhus.lib.core.cfg.CfgProvider;
import de.mhus.lib.core.config.DefaultConfigFactory;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.config.IConfigFactory;
import de.mhus.lib.core.config.MConfig;
import de.mhus.lib.core.logging.Log;

@DefaultFactory(DefaultMApiFactory.class)
public class MCfgManager {

    private HashMap<String, CfgProvider> configurations = new HashMap<>();
    
    //private CentralMhusCfgProvider provider;
    private IApiInternal internal;

    private IConfigFactory configFactory;

    private LinkedList<File> mhusConfigFiles = new LinkedList<>();

    public MCfgManager(IApiInternal internal) {
        this.internal = internal;
        doRestart();
    }

    /**
     * Stop old provider
     * Start new provider
     * Update configurations
     * 
     * @param provider
     */
    public void registerCfgProvider(CfgProvider provider) {
        if (provider == null) return;
        CfgProvider old = configurations.put(provider.getName(), provider);
        if (old != null) old.doStop();
        provider.doStart();
        MApi.getCfgUpdater().doUpdate(provider.getName());
    }

    /**
     * Stop old provider
     * Update configuration
     * @param name
     */
    public void unregisterCfgProvider(String name) {
        CfgProvider old = configurations.remove(name);
        if (old != null) old.doStop();
        MApi.getCfgUpdater().doUpdate(name);
    }

    @Override
    public String toString() {
        return configurations.keySet().toString();
    }

    public Collection<CfgProvider> getProviders() {
        return configurations.values();
    }

    public void startInitiators() {

        MApi.dirtyLogInfo("Start mhu-lib initiators");
        
        TreeMap<String, Object[]> initiators = new TreeMap<>(); // execute in an ordered way
        // default
        initiators.put("001_system", new Object[] {new SystemCfgInitiator(), null});
        initiators.put("002_logger", new Object[] {new LogCfgInitiator(), null});

        // init initiators
        try {
            IConfig system = null;
            CfgProvider provider = configurations.get(MConstants.CFG_SYSTEM);
            if (provider != null)
                system = provider.getConfig();
            if (system != null) {
                MApi.setDirtyTrace(system.getBoolean("log.trace", false));
                Log.setStacktraceTrace(system.getBoolean("stacktraceTrace", false));
            }
            for (String owner : configurations.keySet()) {
                IConfig cfg = configurations.get(owner).getConfig();
                MActivator activator = MApi.get().createActivator();
                if (cfg == null) {
                    MApi.dirtyLogDebug("Config is null for",owner);
                } else
                    for (IConfig node : cfg.getList("initiator")) {
                        try {
                            String clazzName = node.getString("class");
                            String name = node.getString("name", clazzName);
                            String level = node.getString("level", "100");
                            name = level + "_" + name;

                            if ("none".equals(clazzName)) {
                                MApi.dirtyLogDebug("remove initiator", name);
                                initiators.remove(name);
                            } else if (clazzName != null && !initiators.containsKey(name)) {
                                MApi.dirtyLogDebug("add initiator", name);
                                CfgInitiator initiator =
                                        activator.createObject(CfgInitiator.class, clazzName);
                                initiators.put(name, new Object[] {initiator, node});
                            }
                        } catch (Throwable t) {
                            MApi.dirtyLogError("Can't load initiator", node, " Error: ", t);
                        }
                    }
            }

            for (Object[] initiator : initiators.values())
                try {
                    CfgInitiator i = (CfgInitiator) initiator[0];
                    IConfig c = (IConfig) initiator[1];
                    MApi.dirtyLogInfo("run initiator", initiator[0].getClass());
                    i.doInitialize(internal, this, c);
                } catch (Throwable t) {
                    MApi.dirtyLogError("Can't initiate", initiator.getClass(), " Error: ", t);
                }

        } catch (Throwable t) {
            MApi.dirtyLogError("Can't initiate config ", t);
        }
        // MApi.getCfgUpdater().doUpdate(null);
    }
    
    /**
     * The getConfig without default value will return an empty
     * configuration and not null if the configuration is not found.
     * 
     * @param owner
     * @return Always an configuration.
     */
    public IConfig getCfg(Object owner) {
        IConfig ret = getCfg(owner, null);
        if (ret == null) ret = getConfigFactory().create();
        return ret;
    }
    
    /**
     * Returns the found configuration or the default value.
     * 
     * @param owner
     * @param def
     * @return The configuration or def
     */
    public IConfig getCfg(Object owner, IConfig def) {

        Class<?> c = null;
        if (owner instanceof String) {
            String name = (String) owner;
            IConfig cClass = getCfg(name);
            if (cClass != null) {
                //				log().t("found (1)",name);
                return cClass;
            }
        } else if (owner instanceof Class) {
            c = (Class<?>) owner;
        } else {
            c = owner.getClass();
        }
        while (c != null) {
            String name = c.getCanonicalName();
            IConfig cClass = getCfg(name);
            if (cClass != null) {
                //				log().t("found (2)",owner.getClass(),name);
                return cClass;
            }
            c = c.getSuperclass();
        }
        //		log().t("not found",owner.getClass());

        return def;
    }

    public IConfig getCfg(String owner) {

        CfgProvider p = configurations.get(owner);
        if (p != null) {
            IConfig cOwner = p.getConfig();
            if (cOwner != null) return cOwner;
        }
        return new MConfig();
    }

    public IConfig getCfg(String owner, IConfig def) {

        IConfig cClass = getCfg(owner);
        if (cClass != null) {
            //			log().t("found (3)",owner.getClass(),owner);
            return cClass;
        }

        return def;
    }

    public void doRestart() {
        CfgProvider system = configurations.get(MConstants.CFG_SYSTEM);
        if (system != null) {
            configurations.forEach((k,v) -> v.doRestart() );
        } else {
            initialConfiguration();
        }
    }

    protected void initialConfiguration() {
        CentralMhusCfgProvider provider = new CentralMhusCfgProvider();
        registerCfgProvider(provider);
    }

    public synchronized IConfigFactory getConfigFactory() {
        if (configFactory == null)
            configFactory = new DefaultConfigFactory();
        return configFactory;
    }


    public class CentralMhusCfgProvider extends CfgProvider {


        private IConfig systemNode;
        private IConfig config;


        public CentralMhusCfgProvider() {
            super(MConstants.CFG_SYSTEM);
        }
        
        @Override
        public synchronized IConfig getConfig() {
            return systemNode;
        }

        @Override
        public void doStart() {
            doRestart();
        }
        
        @Override
        public void doRestart() {
            
            LinkedList<File> fileList = new LinkedList<>();
            
            File configFile = new File(MApi.getSystemProperty(MConstants.PROP_CONFIG_FILE, null));
            fileList.add(configFile);
            
            if (configFile.exists() && configFile.isFile())
                try {
                    MApi.dirtyLogInfo("Load config file", configFile);
                    config = getConfigFactory().read(configFile);
                    systemNode = config.getObject(MConstants.CFG_SYSTEM);
                    if (systemNode != null) {
                        String includePattern = systemNode.getString("include", null);
                        if (includePattern != null) {

                            File i = new File(includePattern);
                            if (!i.isAbsolute())
                                i = new File(configFile.getParentFile(), includePattern);
                            for (File f : MFile.filter(i.getParentFile(), i.getName())) {
                                
                                if (f.getName().endsWith(".xml") || f.getName().endsWith(".yaml")) {
                                    MApi.dirtyLogInfo("Load config file", f);
                                    IConfig cc = getConfigFactory().read(f);
                                    cc.setString("_source", f.getAbsolutePath());
                                    IConfig.merge(cc, config);
                                    fileList.add(f);
                                }
                            }
                        }
                    } else {
                        systemNode = new MConfig();
                    }

                    for (IConfig owner : config.getObjects()) {
                        if (!owner.getName().equals(MConstants.CFG_SYSTEM)) {
                            registerCfgProvider(new PartialConfigProvider(owner));
                        }
                    }
                    mhusConfigFiles = fileList;
                    return;
                } catch (Exception e) {
                    MApi.dirtyLogDebug(e);
                }

            MApi.dirtyLogDebug("*** MHUS Config file not found", configFile);
            config = new MConfig(); // set empty config
            systemNode = new MConfig();
            
        }

        @Override
        public void doStop() {
            configurations.values().forEach(v -> {
                if (v instanceof PartialConfigProvider)
                    unregisterCfgProvider(v.getName());
            });
        }

    }
    
    private class PartialConfigProvider extends CfgProvider {

        private IConfig config;

        public PartialConfigProvider(IConfig config) {
            super(config.getName());
            this.config = config;
        }

        @Override
        public IConfig getConfig() {
            return config;
        }

        @Override
        public void doStart() {
        }

        @Override
        public void doStop() {
        }

        @Override
        public void doRestart() {
        }

    }

    public List<String> getOwners() {
        return new LinkedList<>(configurations.keySet());
    }

    /**
     * List of files for default mhus-config - inclusive includes. First entry is
     * the mhus-config file
     * @return List of files
     */
    public List<File> getMhusConfigFiles() {
        return mhusConfigFiles;
    }
}