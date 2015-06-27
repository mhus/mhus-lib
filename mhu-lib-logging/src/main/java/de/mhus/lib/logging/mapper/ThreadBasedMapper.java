package de.mhus.lib.logging.mapper;

import de.mhus.lib.core.logging.Log.LEVEL;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.logging.TrailLevelMapper;

public class ThreadBasedMapper implements TrailLevelMapper {
	
	private ThreadLocal<ThreadMapperConfig> map = new ThreadLocal<>();
	
	public void set(ThreadMapperConfig config) {
		map.set(config);
	}

	public void set(String config) {
		if (config == null || !config.startsWith(ThreadMapperConfig.MAP_LABEL)) return;
		ThreadMapperConfig c = new ThreadMapperConfig();
		c.doConfigure(config);
		set(c);
	}
	
	public void release() {
		map.remove();
	}
	
	public ThreadMapperConfig get() {
		ThreadMapperConfig config = map.get();
		if (config == null) return null;
		if (config.isTimedOut()) {
			release();
			return null;
		}
		return config;
	}
	
	@Override
	public LEVEL map(Log log, LEVEL level, Object... msg) {
		ThreadMapperConfig config = get();
		if (config == null) return level;
		return config.map(log, level, msg);
	}

	@Override
	public String doSerializeTrail() {
		ThreadMapperConfig c = get();
		return c == null ? null : c.doSerialize();
	}

	@Override
	public void doConfigureTrail(String backup) {
		if (backup == null) return;
		set(backup);
	}

	@Override
	public void doResetTrail() {
		release();
	}

	@Override
	public boolean isLocalTrail() {
		ThreadMapperConfig c = get();
		return c == null ? true : c.isLocal();
	}
	
}
