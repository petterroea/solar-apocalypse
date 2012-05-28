

package info.nothingspecial.SOLAR_APOCALYPSE;



//import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;



import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;


public class SOLAR_APOCALYPSE extends JavaPlugin implements Runnable{
	static final boolean DEBUG = false;
	static final boolean DEBUG2 = false;
	static final boolean LightingFun = false;
	
	
	private static final String Verson = "ApocV-B12";

	int TICKER = 10;
	
	
	Logger log = Logger.getLogger("Minecraft");//Define your logger
	
	long corectWorldtime =0;
	long lastTick =0;
	int counter = 0;
	boolean ApocRunning = false;
	boolean ApocStart = false;
	
	boolean UseTime = true;
	
	String skip = "";
	double multiplier = 2;
	double doomClock = 0;
	double doomClockStep = 0;
	double StartBurnOnDay = 0;
	double StartFreezeOnDay = 0;
	double StartRandmBlock = 1;
	double StartChunkdestroyer = 3;
	
	double multiplierStep = 1;
	long nextday = 0;
	
	String worldname ="";
	Random rand= new Random();

	protected HashMap<String, Integer> Afected_Block_List = new HashMap<String, Integer>();
	
	
	Random rgen = new Random();  // Random number generator
	int[][] RandLoc = new int[256][2];
	int ChunkCount = 0;
	
	//--- Initialize the array to the ints 0-51

	
	
	
	
	
	
	@Override
	public void onEnable(){

		 new LoginListener(this);

		 
		
		
		
		if(!this.getConfig().getBoolean(Verson)){
		this.getConfig().set(Verson, true);
		this.getConfig().set("ApocRunning", false);
		this.getConfig().set("WorldName", "world");
		this.getConfig().set("SkipDayorNight", "");
		this.getConfig().set("TICKER", 10);
		
		
		this.getConfig().set("Multiplier", 2);
		this.getConfig().set("MultiplierStep", 1);
		
		this.getConfig().set("DoomClock", 1);
		this.getConfig().set("DoomClockStep", 1);
		
		
		this.getConfig().set("StartRandmBlock", 1);
		this.getConfig().set("StartBurnOnDay", 3);
		this.getConfig().set("StartFreezeOnDay", 3);
		this.getConfig().set("StartChunkdestroyerOnDay", 3);
		
		
		String[] listOfStrings = { 
				"WOOD->51", //fire 51
				"LOG->51",
				"WOOD_STAIRS->51",
				"FENCE_GATE->51",
				"NETHERRACK->51",
				"CACTUS->51",
				"FENCE->51",
				"WOOL->51",
				"STEP->51",
				"DOUBLE_STEP->51",
				
				"LONG_GRASS->32",//32 dead bush really grass 31 0
				"YELLOW_FLOWER->32",
				"RED_ROSE->32",
				
				"SAPLING->0", //0 air
				"CROPS->0",
				"LEAVES->0",
				"PUMPKIN->0",
				"PUMPKIN_STEM->0",
				"MELON_STEM->0",
				"DEAD_BUSH->0",
				"BROWN_MUSHROOM->0",
				"RED_MUSHROOM->0",
				"HUGE_MUSHROOM_1->0",
				"HUGE_MUSHROOM_2->0",
				"SNOW->0",
				"ICE->0",
				"VINE->0",
				"SUGAR_CANE_BLOCK->0",
				"WATER_LILY->0",
				"STATIONARY_WATER->0",
				"WATER->0",
				
				"MYCEL->3",// 3 dirt
				"SOIL->3",
				"GRASS->3"};
		this.getConfig().set("Afected_Block_List", Arrays.asList(listOfStrings));
		
		this.saveConfig();
		
		if (DEBUG) log.info("[SOLAR APOCALYPSE DEBUG] Creating New Config.");
		this.reloadConfig();
		}
		
		ApocStart = this.getConfig().getBoolean("ApocRunning");
		skip = this.getConfig().getString("SkipDayorNight");
	
		
		
		
		
		
		
		TICKER = this.getConfig().getInt("TICKER") +5;
		if (TICKER <5) TICKER = 5;
		
		multiplier = this.getConfig().getDouble("Multiplier");
		if (multiplier <= 0){
			multiplier = 1;
			UseTime = false;
		}
		multiplierStep = this.getConfig().getDouble("MultiplierStep");
		if (multiplierStep < 0) multiplierStep = 0;
		
		
		
		doomClock = this.getConfig().getDouble("DoomClock");
		if (doomClock < 0) doomClock = 0;
		doomClockStep = this.getConfig().getDouble("DoomClockStep");
		if (doomClockStep < 0) doomClockStep = 0;
		
	
		

		StartRandmBlock = this.getConfig().getDouble("StartRandmBlock");
		StartBurnOnDay = this.getConfig().getDouble("StartBurnOnDay");
		StartFreezeOnDay = this.getConfig().getDouble("StartFreezeOnDay");
		StartChunkdestroyer = this.getConfig().getDouble("StartChunkdestroyerOnDay");
	
		worldname = this.getConfig().getString("WorldName");  
		
		//getWorld (String name)
		World world = getServer().getWorld(worldname);
		
		if (world == null) log.info("[SOLAR APOCALYPSE DEBUG] Error: No world with name " + worldname);
		
		corectWorldtime = world.getFullTime() / 24000 * 24000;
		
	
		getServer().getScheduler().scheduleSyncRepeatingTask(this, this, TICKER, TICKER);
	
		
	
		
		List<String> burnlist = this.getConfig().getStringList("Afected_Block_List");
		
		for(int j = 0; j < burnlist.size(); j++){
			String str = burnlist.get(j).replaceAll("\\s+","");
			
			String[] tmp = str.split("->");
			
			if (tmp.length == 2){
				int aInt = Integer.parseInt(tmp[1]);
				if (DEBUG) log.info("[SOLAR APOCALYPSE DEBUG] " + tmp[0]  + " -> " + aInt  );
				Afected_Block_List.put(tmp[0], aInt);
			}else{
				log.info("[SOLAR APOCALYPSE DEBUG] Afected_Block_List rejected "+ str);
			}
			Afected_Block_List.put("FIRE", 0);
			//Afected_Block_List.put("TORCH", 0);
			
		}
		
		
		
		int count = 0;
		for (int i1=0; i1<16; i1++) 
			for (int i2=0; i2<16; i2++) {
				RandLoc[count][0] = i1;
				RandLoc[count][1] = i2;
				count = count +1;
			}
		
		

		
		
		//--- Shuffle by exchanging each element randomly
		for (int i=0; i<RandLoc.length; i++) {
		    int randomPosition = rgen.nextInt(RandLoc.length);
		    int[] temp = RandLoc[i];
		    RandLoc[i] = RandLoc[randomPosition];
		    RandLoc[randomPosition] = temp;
		}
		
		
		
		
		
		
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {

			   public void run() {
				   World world = getServer().getWorld(worldname);
				   
				   if (!ApocRunning) return;
				   if (!isNighttime(world.getTime())) return;
				   
				   
				   for (Player player : world.getPlayers()) {
					  
					   if (player.getLocation().getBlock().getLightLevel() > 11 ) return;
						
					   
					Location loc = player.getEyeLocation();
					
					//double radians = Math.toRadians(loc.getYaw());
			
				   //loc.add(-Math.sin(radians), 0, Math.cos(radians));
				        
					

					 
					int dir=4; //(Up or middle ?)	
					
				      float y = player.getLocation().getYaw();
				         if( y < 0 ){y += 360;}
				         y %= 360;
				         
				         
				         
				         int i = (int)((y+8) / 45);
				         
				         
				         
				         switch(i){
				       
				         case 0:	
				         case 8:
								dir = 7; //		 North	
								break; 
				         
				         case 1:	
						        dir = 6; //	North East	
								break;
								
								
				         case 2:	
						        dir = 3; //		 East	
								break;
					        	 						
								
				         case 3:
				        	 	dir = 0; //	 South East
				        	 	break;	
								
								
				         case 4:
				        	 	dir = 1; //	 South
				        	 	break;
				       
				         case 5:	
					        	dir = 2; // South West	
								break;
								
				         case 6:	
					        	dir = 5; //		 West	
								break;
								
				         case 7:	
								dir = 8; //		 North	 west
								break;
								
				         
							
					       	
				         }
				         
				        // try {Thread.sleep(rand.nextInt(100));}
			        	 //catch (Exception e) {}
				         
				         player.playEffect(loc, Effect.SMOKE, dir);
				       
		
					
					 }
				

		
				
			   }
			}, 40L, 40L);
		
		
		
		
		
		
	
		
		
		if (ApocStart) world.setTime(0);
		
	}

	

	 

	



		
	








	@Override
	public void run() {
		

		boolean AdvanceDay2=false;
		
		World world = getServer().getWorld(worldname);
		
		
		if (( world.getTime() <= (TICKER + 1))&&(ApocStart)){
			ApocRunning = true;
			ApocStart = false;
			//getServer().broadcastMessage(ChatColor.RED + "Solar Apocalypse Has Begun!");
			 for (Player player : getServer().getWorld(worldname).getPlayers()) 
				 	player.sendMessage(ChatColor.RED + "Solar Apocalypse Has Begun!");
					 log.info("[SOLAR APOCALYPSE] Has Begun");	
			
		}else{
			AdvanceDay2=true;
			
		}
			
		
		
		if (!ApocRunning) return;
		
		if ((skip.equalsIgnoreCase("day"))||(skip.equalsIgnoreCase("night"))) UseTime = false;
		
		if (skip.equalsIgnoreCase("")) skip = "none";
		
		boolean AdvanceDay= false;
	
		
		
		
		
		
	
	 counter = counter + 1;
		if (!UseTime){
			counter=1;
			multiplier = 1;
		}
		
		
		
		
		
		if (counter < multiplier) {
				if (UseTime){
			
				world.setTime(world.getTime()-TICKER);
				if (DEBUG) log.info("[SOLAR APOCALYPSE DEBUG] Tick = " + counter + " multiplier = " + multiplier +" Time = " + world.getTime());	
			
				}
		}else{
		
			counter = 0;
			if (DEBUG) log.info("[SOLAR APOCALYPSE DEBUG] tock "+ world.getTime() + " lastTick = " +lastTick);	
			
			
			
			
			 if (world.getTime() < lastTick)
				AdvanceDay= true;
			lastTick = world.getTime();
			
			
			if ( (skip.equalsIgnoreCase("night")) && !(isDaytime(world.getTime())) ) //skip night
				world.setTime(0);
				
			
				
			
			
			if ( (skip.equalsIgnoreCase("day")) &&  !(isNighttime(world.getTime()))) //skip Day
				world.setTime(14000);
			
		
			if (DEBUG) log.info("[SOLAR APOCALYPSE DEBUG]  " + counter + " multiplier = " + multiplier +" Time = " + world.getTime());	
		}
		
	
		
		

		
	
		
		 
	
		
		 
		
		 if ((AdvanceDay) && (AdvanceDay2) ){
			 AdvanceDay=false;
			 AdvanceDay2=false;
			 
			corectWorldtime = corectWorldtime + 24000;		 
			if (UseTime) world.setFullTime(corectWorldtime);
			 
			multiplier = multiplier + multiplierStep;
			nextday = 0;
			doomClock = doomClock + doomClockStep;
			if (multiplier > 10) multiplier = 10;
			if (doomClock > 10) doomClock = 10;
			
			this.getConfig().set("Multiplier", multiplier);
			this.getConfig().set("DoomClock", doomClock);
			this.saveConfig();
			
			log.info("[SOLAR APOCALYPSE DEBUG]  Doom Clock Chime " + doomClock + " time = " + world.getTime()  );	
			
			
			//world.getPlayers()
			
			 for (Player player : world.getPlayers()) 
					 player.sendMessage(ChatColor.RED +"Doom Clock Chime");
			 
			
			
		
			
			
					 log.info("[SOLAR APOCALYPSE] Doom Clock =" + doomClock);	
			 //getServer().broadcastMessage(ChatColor.RED +"Doom Clock Chime");
		 }
		 
		 List<org.bukkit.entity.Entity> entitylist = world.getEntities();
		
					
					if (isDaytime(world.getTime())){
						world.setStorm(false);
					world.setThundering(false);
					
					}else{
						if (world.getTime() > 0) world.setThundering(true);
					}
		 
			for(int j = 0; j < entitylist.size(); j++){
				Entity entity = entitylist.get(j);
	       	 
			
				Boolean CanSeeSky = (world.getHighestBlockYAt(entity.getLocation().getBlock().getLocation()) == entity.getLocation().getBlock().getY());//can see sky
					
				 if (isNighttime(world.getTime())) //night time
					 if (entity.getLocation().getBlock().getLightLevel() < 10) //12
							 if (doomClock >= StartFreezeOnDay)
								 if (entity instanceof LivingEntity)
									 if (!(entity instanceof Monster)){
									 
										 if (CanSeeSky)
											 ((LivingEntity) entity).damage(2);  // damage living non monsters when away from lights.
										
											 if (entity instanceof Player){
												 Player player = (Player) entity;
												 
												 ItemStack[] amrorList = player.getInventory().getArmorContents();
												 
												 //298-301
												 
												 int fullLeather = 0;
												for(int A = 0; A < amrorList.length; A++){
													
													if (amrorList[A] != null)
														if ((amrorList[A].getTypeId() >= 298) && (amrorList[A].getTypeId() <= 301))
															fullLeather = fullLeather +1;
														
													
												}
												 
												 
												 
												 
												 if (fullLeather == 4){
													
												 }else{
												 
												 
												 if (player.getHealth() < 2){
													 player.getLocation().getBlock().getRelative(0, 1, 0).setTypeId(79);
													 player.getLocation().getBlock().setTypeId(79);
												 }else
												 player.damage(2);
										
												 }
												 
												 
											 }
								 }
					 
				 
					 
				 
				 
				 if (!(entity instanceof LivingEntity))  // items outside decay faster
					 if ((doomClock >= StartBurnOnDay) && (CanSeeSky)) 
						 if (entity.getTicksLived() > 500)  entity.remove();
					 
						
				if (( isDaytime(world.getTime())) && (CanSeeSky))//day time
					if (doomClock >= StartBurnOnDay) entity.setFireTicks(TICKER+10);

				
	       	 
	        }


			
		
			
		

			boolean dobiome=true;
				
			if (dobiome){ //biome stuff	
				Chunk[] chunks	= world.getLoadedChunks();
				List<Chunk> list = new ArrayList<Chunk>();// = Arrays.asList(world.getLoadedChunks());
				int test3 = 22; //skip day
				
				if (isDaytime(world.getTime())) test3 = 19;
				
				for (int j2=0; j2 < chunks.length; j2++) 
					if (chunks[j2].getBlock(0, 0, 0).getTypeId() != test3)
						list.add(chunks[j2]);
				
				
				if (list.size() == 0){
					if (DEBUG) log.info("[SOLAR APOCALYPSE DEBUG] chunk list is done");
				}else{
					if (DEBUG) log.info("[SOLAR APOCALYPSE DEBUG] changing biome remining chunks " +list.size() + " of " + chunks.length);
					
					
					Chunk chunk = list.get(rand.nextInt(list.size()));
					if (test3 == 19)
						BiomeChanger(chunk, Biome.DESERT);
					else
						BiomeChanger(chunk, Biome.ICE_PLAINS);
					
						Block keyblock = chunk.getBlock(0, 0, 0);
						keyblock.setTypeId(test3);
						keyblock.getRelative(0, 1, 0).setTypeId(7); //cover key block with bedrock 
						
						
						
						
						
							
				}
			}
			
			
			
			
			
			if (doomClock >= StartChunkdestroyer){
				Chunk[] chunks	= world.getLoadedChunks();
				List<Chunk> list = new ArrayList<Chunk>();// = Arrays.asList(world.getLoadedChunks());
				
				int test2 = 22; //skip day
				
				if (isDaytime(world.getTime())) test2 = 19;
				
				for (int j2=0; j2 < chunks.length; j2++) 
					if (chunks[j2].getBlock(1, 0, 0).getTypeId() != test2)
						list.add(chunks[j2]);
				
				
				if (list.size() == 0){
					if (DEBUG) log.info("[SOLAR APOCALYPSE DEBUG] chunk list is done");
					Chunk tmp = chunks[rand.nextInt(chunks.length)];
					
					
					tmp.getBlock(1, 0, 0).setTypeId(0); //force upkeep on a random chunk
					
					
					if ((isNighttime(world.getTime()) ) && (rand.nextInt(1000)==0))
						for (int y=255; y>0; y=y-2){
							if (tmp.getBlock(8, y, 8).getTypeId() == 0){

								tmp.getWorld().strikeLightningEffect(tmp.getBlock(8, y, 8).getLocation());
							
							}else{
								tmp.getWorld().strikeLightning(tmp.getBlock(8, y, 8).getLocation());
								tmp.getWorld().createExplosion(tmp.getBlock(8, y, 8).getLocation(),10);
								break;
							}
								
							
						}
					
					
					
				}else{
					
					ChunkCount = list.size();
					
					Chunk chunk = list.get(rand.nextInt(list.size()));
					
						ChunkDestroyer(chunk, 666, true);
						Block keyblock = chunk.getBlock(1, 0, 0);
						keyblock.setTypeId(test2);
						keyblock.getRelative(1, 1, 0).setTypeId(7); //cover key block with bedrock 
						
				}
				
			
					
				
				
			}
				
		
			
				
			if (doomClock >= StartRandmBlock){	
		
			List<Chunk> list = new ArrayList<Chunk>();// = Arrays.asList(world.getLoadedChunks());
			List<Player> players	= world.getPlayers();
				
			
			
			
			
				int flub = 3;
				
			if (players.size() == 0) return;
				for (int je=0; je < players.size(); je++) {
					Chunk chunk = players.get(je).getLocation().getChunk();
					list.add(chunk);
					
					
					for (int x=0; x<(flub *2)+1; x++) for (int z=0; z<(flub *2)+1; z++) 
							list.add( world.getChunkAt(chunk.getX() - flub + x, chunk.getZ() - flub + z) );
		
					
					list.add( world.getLoadedChunks()[ rand.nextInt(world.getLoadedChunks().length) ]);
					
					
				}
				
				
					
				
				
				
				
			
			int T =(((int)doomClock * 5) * players.size());
				for (int B=0; B < 1000; B++){
						Chunk Tmp = list.get(rand.nextInt(list.size()));
						int x = Tmp.getX() * 16 + rand.nextInt(16);
						int z = Tmp.getZ() * 16 + rand.nextInt(16);
						if (RainofDoom(Tmp.getWorld(), x, z, false))
							T=T-1;
							
						if (T < 0 ) break;
				}
						
			
			}	
			
			
			
					
				
						
	}
	
	public void onDisable(){}

	
	
	
	

		

		



		public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
			
			

			if (args.length > 1) return false;
			 if (args.length > 0) {
				        	
				        	
				        	
				        	
				      
					if (args[0].equalsIgnoreCase("start"))
						if (ApocRunning){
								sender.sendMessage("Apocalypse is allready running");
								return true;
						}else{
							sender.sendMessage("Solar Apocalypse will start at sunrize!");
							this.getConfig().set("ApocRunning", true);					
							this.saveConfig();
							ApocStart = true;
							return true;
						
						}
					
					
				    
					if (args[0].equalsIgnoreCase("start-day"))
						if ( skip.equalsIgnoreCase("") ){
				
							sender.sendMessage("Solar Apocalypse Day Mode will start at sunrize!");
							this.getConfig().set("ApocRunning", true);
							this.getConfig().set("SkipDayorNight", "Night");
							this.getConfig().set("DoomClockStep", 0.5);
							this.saveConfig();
							doomClockStep = 0.5;
							skip = "Night";
							ApocStart = true;
							return true;
						
						}else{
							//
							sender.sendMessage("Apocalypse type already is set");
							return true;
						}
						
					
					if (args[0].equalsIgnoreCase("start-night"))
						if ( skip.equalsIgnoreCase("") ){
				
							sender.sendMessage("Solar Apocalypse Night Mode will start at sunrize!");
							this.getConfig().set("ApocRunning", true);
							this.getConfig().set("SkipDayorNight", "Day");
							this.getConfig().set("DoomClockStep", 0.5);
							this.saveConfig();
							doomClockStep = 0.5;
							ApocStart = true;
							skip = "Day";
							return true;
						
						}else{
							//
							sender.sendMessage("Apocalypse type already is set");
							return true;
						}
					
					
					
					
					if (args[0].equalsIgnoreCase("stop"))
						if (ApocRunning){
						
							 for (Player player : getServer().getWorld(worldname).getPlayers()) 
								 	player.sendMessage(ChatColor.RED + "Solar Apocalypse Paused!");
							
							
							//getServer().broadcastMessage(ChatColor.RED + "Solar Apocalypse Paused!"); 
									 log.info("[SOLAR APOCALYPSE] Paused");	
							this.getConfig().set("ApocRunning", false);
							this.saveConfig();
							ApocRunning = false;
							ApocStart = false;
							skip = "Night";
							return true;
						
						}else{
							sender.sendMessage("Apocalypse is not running");
							return true;
						}
					
			
					
					
					if(args[0].equalsIgnoreCase("?")){
						sender.sendMessage(ChatColor.UNDERLINE + "--Solar Apocalypse Help--");
						sender.sendMessage("/Apoc ?" + ChatColor.GRAY + " Show This Help");
						sender.sendMessage("/Apoc" + ChatColor.GRAY + " Show Apoc Info");
						sender.sendMessage("/Apoc Start" + ChatColor.GRAY + " Start the Solar Apocalypse");
						sender.sendMessage("/Apoc Start-Day" + ChatColor.GRAY + " Start the Solar Apocalypse Days Only");
						sender.sendMessage("/Apoc Start-Night" + ChatColor.GRAY + " Start the Solar Apocalypse Nights Only");
						sender.sendMessage("/Apoc Stop" + ChatColor.GRAY + " Pause the Solar Apocalypse");
						
						
						
						return true;
			    	}
					
					
					
					return false;
			 }else{

		    	
			 
			
			
			 	sender.sendMessage(ChatColor.UNDERLINE + "--Solar Apocalypse Status--");
			 	
			 	
			 	
			 	
			 	if ( (ApocRunning) && (!ApocStart) )
		    		sender.sendMessage("Apocalypse is running");
			 	if ( (!ApocRunning) && (!ApocStart) )
		    		sender.sendMessage("Apocalypse is not running");
				if ( (!ApocRunning) && (ApocStart) )
		    		sender.sendMessage("Apocalypse Will Start at Sunrize");
		    	
		    		
				
		    		
		    		
		    		if ((!skip.equalsIgnoreCase("")) &&   (ApocRunning)){
		    			double t = getServer().getWorld(worldname).getTime();
		    			if ((skip.equalsIgnoreCase("day"))  ||  (skip.equalsIgnoreCase("night")) )
		    					sender.sendMessage("skiping "+ skip);
		    			
		    			t = ((24000 - t) / 24000) * 20;

		    			t = (double) (t * multiplier);
		    			sender.sendMessage("In "+ (int)t + " Mins The Doom Clock Chime will strike "+ (doomClock + doomClockStep));	
		    			
		    			if (ChunkCount > 0)
		    				sender.sendMessage("Chunks to Destroy "+ ChunkCount + " of " + getServer().getWorld(worldname).getLoadedChunks().length);	
		    		}
		    	
		    		
		    	
		    		
		    	
		    	return true;
			 }
		}
		
		
		
		
		

		public Boolean isRunning() { return ApocRunning;}	
		
		public boolean FireSpread() { return true;}
		
	
		
		public Boolean isCorectWorld(World world) { return world == getServer().getWorld(worldname);}	


		public boolean isDaytime(long time) {		
			long today = time % 24000;
			return today >= 0 && today < 12000;}

		
		public boolean isNighttime(long time) {
			long today = time % 24000;
			return today >= 14000 && today < 22000;}



		
		
		
		
	
		public void ChunkDestroyer (Chunk chunk, int NumOfChanges, boolean Penatrate) {
			if (chunk == null) return;
			if (!chunk.isLoaded()) return;
			
			if (NumOfChanges > RandLoc.length) NumOfChanges = RandLoc.length;
			for (int i=0; i<RandLoc.length; i++) {
				int x = chunk.getX() * 16 + RandLoc[i][0];
				int z = chunk.getZ() * 16 + RandLoc[i][1];
				if (RainofDoom(chunk.getWorld(), x, z, Penatrate))
					NumOfChanges = NumOfChanges -1;
				if (NumOfChanges < 0) break;
			}
			
			if (NumOfChanges == RandLoc.length)
				if ((isNighttime(chunk.getWorld().getTime()) && (rand.nextInt(20)==0))){ 

					chunk.getWorld().strikeLightningEffect(chunk.getWorld().getHighestBlockAt(chunk.getBlock(8, 0, 8).getLocation()).getLocation());
				
				}
			
			
		}
		
		
		
		public void BiomeChanger (Chunk chunk, Biome biome) {
			
			
			for (int x=0; x<16; x++) {
				for (int z=0; z<16; z++){
				chunk.getBlock(x, 0, z).setBiome(biome);
			}
			}
				
			chunk.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
	
		}
		
		
		
		
		
		public boolean RainofDoom(World world, int x, int z, boolean Penatrate)	{
			boolean madechange = false;
			
			for (int y=255; y>0; y--){
				
				
				int rainr = ChangeThisTo(world.getBlockAt(x, y, z), true, Penatrate);
				
				
					
					
				if ((rainr == -3) || (rainr == -4)) madechange = true;
				if ((rainr == -3) || (rainr == -1)) break;
				if ((!Penatrate) && (madechange)) break;
				
				
			
				
			}
			return madechange;
		}
		
		
		
		public int ChangeThisTo(Block block, boolean makechange, boolean Penatrate) {
			//-1 no change and stop
			//-2 no change keep going
			//-3 made a change and stop
			//-4 made a change keep going
			
			if (!makechange) Penatrate = false; //if not making a change cant Penatrate;
			
			
			int changeto = -1; //-1 nothing changed stop looking
			if (block.getTypeId() == 0) changeto = -2; //-2 if air keep going
		
			
			
			
			
			if ( Afected_Block_List.containsKey(block.getType().name()) ){
				
				changeto = Afected_Block_List.get(block.getType().name());
				
				boolean canpass = false;
				switch (block.getType()){
				
	
				case SNOW:
					canpass = true;
				case ICE:
				case WATER:
				case STATIONARY_WATER:
					if (isNighttime(block.getWorld().getTime())) changeto = -1;

					break;
					
				case FIRE:
					if (isDaytime(block.getWorld().getTime())) changeto = -1;
					if (isNighttime(block.getWorld().getTime())) changeto = 0;
					break;
				case STEP:
				case DOUBLE_STEP:
					changeto = -1;
					if (block.getData() == 2) block.setTypeId(51);
					break;
			
					
					
				}
				
				
				
				
				switch (changeto){
				case  51: //fire 
					
					if (doomClock >= StartBurnOnDay) 
						if (isDaytime(block.getWorld().getTime())) 
							block.getRelative(0, 1, 0).setTypeId(51);
				
					changeto = -1;
					canpass = false;

					break;
				case 0:
					canpass = true;
					break;
					
				}
				
				
				
				if (changeto == -1) makechange = false;
			
					
				
				if (makechange){
					
					if (changeto >= 0){ 
						if (DEBUG) log.info("[SOLAR APOCALYPSE DEBUG] " +block.getType().name()  + " changeto " + changeto);
						block.setTypeIdAndData(changeto, (byte) 0, false); 
						
						
						if (LightingFun)block.getWorld().strikeLightningEffect(block.getLocation());
					}
					changeto = -3;
					if (canpass) changeto = -4;
					
				}
				
				
				
			}else{
			
		
			if (block.getTypeId() != 0){
			
				 Block Test = block.getWorld().getHighestBlockAt(block.getLocation());
				 
				 
				 if (Test.getY() < block.getY())
					 changeto = -2;
				
			
				}		
			}
			
			
			
			return changeto; 
		}

	
	
	
}// the end!

