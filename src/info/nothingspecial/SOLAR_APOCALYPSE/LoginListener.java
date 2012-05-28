package info.nothingspecial.SOLAR_APOCALYPSE;


import java.util.Random;
import java.util.logging.Logger;








import org.bukkit.Material;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.player.PlayerInteractEvent;











 
public class LoginListener implements Listener {

	Logger log = Logger.getLogger("Minecraft");//Define your logger
	SOLAR_APOCALYPSE Apoc;
	Random rand= new Random();
	
    public LoginListener(SOLAR_APOCALYPSE plugin) {
    	Apoc = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
 
    

    
    
    
    
    
    
     
    
   /* 
	@EventHandler (priority = EventPriority.LOW)
	public void onCreatureSpawnEvent (CreatureSpawnEvent event){
		//if (!Apoc.isRunning()) return;
		//if (!Apoc.isCorectWorld(event.getEntity().getWorld() )) return;
		World world = event.getEntity().getWorld();
		Entity entity = event.getEntity();
		Boolean CanSeeSky = (world.getHighestBlockYAt(entity.getLocation().getBlock().getLocation()) == entity.getLocation().getBlock().getY());//can see sky
		
		
		
		
		
		
	
		
		if (( event.getSpawnReason() == SpawnReason.NATURAL ) || ( event.getSpawnReason() == SpawnReason.CHUNK_GEN )){
		
			
			
			//
			
			
			//if ( event.getEntity().getWorld().getHighestBlockYAt(event.getEntity().getLocation()) == event.getEntity().getLocation().getY()+1) )
			
			//if (event getHighestBlockYAt(event.getSource().getLocation()) == event.getSource().getY()+1) {
			if (CanSeeSky) {
			event.getEntity().getWorld().spawnCreature(event.getLocation(), EntityType.BLAZE);
			
			event.setCancelled(true);
			
			log.info("event.getSpawnReason() " + event.getSpawnReason().name() + " type = "+ event.getEntityType().getName() );
			}
		}
		
    }
    */
	
	
	
    
	@EventHandler (priority = EventPriority.LOW)
	public void onBlockSpread (BlockSpreadEvent event){
		if (!Apoc.isRunning()) return;
		if (!Apoc.isCorectWorld(event.getBlock().getWorld())) return;
		
		
		
		
		if (event.getSource().getType() != Material.FIRE ){
			if (event.getSource().getWorld().getHighestBlockYAt(event.getSource().getLocation()) == event.getSource().getY()+1) {
				
		
				Apoc.ChangeThisTo(event.getSource(), true, false);
				Apoc.ChangeThisTo(event.getSource().getRelative(0, 1, 0), true, false);
				
				
			}
				
	
			if (event.getBlock().getWorld().getHighestBlockYAt(event.getBlock().getLocation()) == event.getBlock().getY()+1) 
				event.setCancelled(true); //can see sky don't spread
		
		}else
			event.setCancelled(Apoc.FireSpread());
	} 
	
	
	
	@EventHandler (priority = EventPriority.LOW)
	public void onBlockPlaceEvent (BlockPlaceEvent event){
	
	}
	
	
	
	
	@EventHandler (priority = EventPriority.LOW)
	public void onBlockFromTo (BlockFromToEvent event){
		if (!Apoc.isRunning()) return;
		if (!Apoc.isCorectWorld(event.getBlock().getWorld())) return;
		 
		
		if (event.getBlock().getWorld().getHighestBlockYAt(event.getBlock().getLocation()) == event.getBlock().getY()+1){

			//event.setCancelled(true); //can see sky dont flow
		}
			
	
		
	}
	
	
	
	

	@EventHandler (priority = EventPriority.LOW)
	public void onBlockFormEvent (BlockFormEvent event){
		if (!Apoc.isRunning()) return;
		if (!Apoc.isCorectWorld(event.getBlock().getWorld())) return;
		 
		
		
		if ((event.getNewState().getType() == Material.SNOW) || (event.getNewState().getType() == Material.ICE)){
			if (Apoc.isDaytime(event.getBlock().getWorld().getTime()))
				event.setCancelled(true);
				return;
			}
		
			
	
		
	}
	
	@EventHandler (priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		
		if (event == null) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (event.getClickedBlock().getWorld() == null) return;
		if (!Apoc.isCorectWorld(event.getClickedBlock().getWorld())) return;
		if (event.getClickedBlock() == null) return;
		if (event.getClickedBlock().getType() == Material.BED_BLOCK){

			event.getPlayer().setBedSpawnLocation(event.getClickedBlock().getLocation());
			event.getPlayer().sendMessage("Your spawn location has been set to this bed.");
		}
		
	}
	
	
}