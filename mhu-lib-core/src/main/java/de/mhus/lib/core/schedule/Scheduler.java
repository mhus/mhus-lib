package de.mhus.lib.core.schedule;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.mhus.lib.core.MThread;

public class Scheduler {

	private Timer timer;
	SchedulerQueue queue = new QueueList();
	private String name = Scheduler.class.getCanonicalName();
	private LinkedList<SchedulerJob> running = new LinkedList<>();
	
	public Scheduler() {}
	
	public Scheduler(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void start() {
		if (timer != null) return;
		timer = new Timer(name,true);
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				doTick();
			}
		}, 1000, 1000);
	}
	
	protected void doTick() {
			List<SchedulerJob> pack = queue.removeJobs(System.currentTimeMillis());
			if (pack == null) return;
			for (SchedulerJob job : pack) {
				try {
					doExecuteJob(job);
				} catch (Throwable t) {
					job.doError(t);
				}
			}
	}

	protected void doExecuteJob(SchedulerJob job) {
		if (!job.startRunning()) return;
		new MThread(new MyExecutor(job)).start(); //TODO unsafe, monitor runtime use timeout or long runtime warnings, use maximal number of threads. be sure a job is running once
	}

	public void stop() {
		if (timer == null) return;
		timer.cancel();
		timer = null;
	}
	
	public void schedule(SchedulerJob scheduler) {
		scheduler.doSchedule(queue);
	}
	
	private class MyExecutor implements Runnable {

		private SchedulerJob job;

		public MyExecutor(SchedulerJob job) {
			this.job = job;
		}

		@Override
		public void run() {
			synchronized (running) {
				running.add(job);
			}
			try {
				doExecuteJob(job);
			} catch (Throwable t) {
				job.doError(t);
			}
			try {
				job.doSchedule(queue);
			} catch (Throwable t) {
				job.doError(t);
			}
			synchronized (running) {
				running.remove(job);
			}
			job.stopRunning();
		}
		
	}

	
	public List<SchedulerJob> getRunningJobs() {
		synchronized (running) {
			return new LinkedList<>(running);
		}
	}
	
	public List<SchedulerJob> getScheduledJobs() {
		return queue.getJobs();
	}
	
}
