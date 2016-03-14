package com.emoniph.witchery.entity;

import com.gamerforea.eventhelper.util.EventUtils;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.brewing.EntityThrowableBase;
import com.emoniph.witchery.client.particle.NaturePowerFX;
import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.util.CreatureUtil;
import com.emoniph.witchery.util.TimeUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityGrenade extends EntityThrowableBase {

   boolean blockPlaced;
   int blockX;
   int blockY;
   int blockZ;


   public EntityGrenade(World world) {
      super(world);
      this.setSize(0.25F, 0.25F);
      super.noClip = false;
   }

   public EntityGrenade(World world, EntityLivingBase thrower, ItemStack stack) {
	  super(world, thrower, -20.0F);
      this.setSize(0.25F, 0.25F);
      super.noClip = false;
   }

   public EntityGrenade(World world, double x, double y, double z, ItemStack stack) {
      super(world, x, y, z, -20.0F);
      this.setSize(0.25F, 0.25F);
      super.noClip = false;
   }

   protected float getGravityVelocity() {
      return this.getImpact()?0.0F:0.05F;
   }

   protected float func_70182_d() {
      return 0.75F;
   }

   protected float func_70183_g() {
      return -20.0F;
   }

   protected void entityInit() {
      super.dataWatcher.addObject(6, Byte.valueOf((byte)0));
      //this.entityInit(); //LeRioN Fix
   }

   protected void setImpact(boolean impact) {
      this.getDataWatcher().updateObject(6, Byte.valueOf((byte)(impact?1:0)));
   }

   public boolean getImpact() {
      return this.getDataWatcher().getWatchableObjectByte(6) == 1;
   }

   protected int getMaxGroundTicks() {
      return super.getMaxGroundTicks();
   }

   protected int getMaxAirTicks() {
      return super.getMaxAirTicks();
   }

   protected void onImpact(MovingObjectPosition mop) {
      if(!super.worldObj.isRemote) {
         this.setImpact(true);
      }

      super.motionX = 0.0D;
      super.motionY = 0.0D;
      super.motionZ = 0.0D;
   }

   public void onEntityUpdate() {
      super.onEntityUpdate();
   }

   public void onUpdate() {
      super.onUpdate();
	  //EntityPlayer player = (EntityPlayer)EntityGrenade.thrower;
      if(super.worldObj.isRemote && this.getImpact() && super.worldObj.rand.nextInt(4) == 0) {
         float var8 = 1.0F;
         float var10 = 1.0F;
         float var12 = 0.0F;
         Witchery.proxy.generateParticle(super.worldObj, super.posX - 0.1D + super.worldObj.rand.nextDouble() * 0.2D, super.posY + 0.3D * (double)super.height - 0.1D + super.worldObj.rand.nextDouble() * 0.2D, super.posZ - 0.1D + super.worldObj.rand.nextDouble() * 0.2D, var8, var10, var12, 10, -0.3F);
      } else if(!super.worldObj.isRemote && !super.isDead) {
         if(!this.blockPlaced && super.ticksExisted % 5 == 4) {
            this.blockPlaced = true;
            this.blockX = MathHelper.floor_double(super.posX);
            this.blockY = MathHelper.floor_double(super.posY);
            this.blockZ = MathHelper.floor_double(super.posZ);
			//super.worldObj.setBlock(this.blockX, this.blockY, this.blockZ, Witchery.Blocks.LIGHT); //LeRioN Fix
        
		} else if(this.blockPlaced && (super.ticksExisted % 5 == 2 || this.getImpact())) {
            int entity = MathHelper.floor_double(super.posX);
            int list = MathHelper.floor_double(super.posY);
            int d0 = MathHelper.floor_double(super.posZ);
            if(this.blockX != entity || this.blockY != list || this.blockZ != d0 || super.ticksExisted % 30 == 4 && super.worldObj.isAirBlock(entity, list, d0)) {
               			               	  //LeRioN fix
				if (!EventUtils.cantBreak((EntityPlayer)super.getThrower(), this.blockX, this.blockY, this.blockZ)) 
			   super.worldObj.setBlockToAir(this.blockX, this.blockY, this.blockZ);
               this.blockX = entity;
               this.blockY = list;
               this.blockZ = d0;
			   //super.worldObj.setBlock(this.blockX, this.blockY, this.blockZ, Witchery.Blocks.LIGHT);//LeRioN Fix
            }
         }

         if(this.getImpact()) {
            Object var7 = null;
            List var9 = super.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, super.boundingBox.addCoord(super.motionX, super.motionY, super.motionZ).expand(1.0D, 1.0D, 1.0D));
            double var11 = 0.0D;

            for(int j = 0; j < var9.size(); ++j) {
               EntityLivingBase entity1 = (EntityLivingBase)var9.get(j);
               if(entity1.canBeCollidedWith() && CreatureUtil.isUndead(entity1)) {
                  entity1.setFire(3);
               }
            }
         }
      }

   }

   protected void onSetDead() {
      if(!super.worldObj.isRemote) {
         this.entityDropItem(Witchery.Items.GENERIC.itemQuartzSphere.createStack(), 0.5F);
         if(this.blockPlaced) {
            this.blockPlaced = false;
			               	  //LeRioN fix
		if (!EventUtils.cantBreak((EntityPlayer)super.getThrower(), this.blockX, this.blockY, this.blockZ)) 
            super.worldObj.setBlockToAir(this.blockX, this.blockY, this.blockZ);
			//LeRioN Fix
         }

         Object entity = null;
         List list = super.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, super.boundingBox.addCoord(super.motionX, super.motionY, super.motionZ).expand(3.0D, 2.0D, 3.0D));
         double d0 = 0.0D;

         for(int j = 0; j < list.size(); ++j) {
            EntityLivingBase entity1 = (EntityLivingBase)list.get(j);
            if(entity1.canBeCollidedWith()) {
               if(CreatureUtil.isUndead(entity1)) {
                  entity1.setFire(5);
                  if(entity1 instanceof EntityPlayer) {
				  
                     EntityPlayer player = (EntityPlayer)entity1;
                     ExtendedPlayer playerEx = ExtendedPlayer.get(player);
                     if(playerEx.getVampireLevel() == 4 && playerEx.canIncreaseVampireLevel()) {
                        
						//if (!EventUtils.cantDamage(entity1, playerEx)) //LeRioN Fix Херня какая-то...
						if(playerEx.getVampireQuestCounter() >= 9) {
                           playerEx.increaseVampireLevel();
                        } else {
                           playerEx.increaseVampireQuestCounter();
                        }
                     }
                  }
               }

               entity1.addPotionEffect(new PotionEffect(Potion.blindness.id, TimeUtil.secsToTicks(super.worldObj.rand.nextInt(3) + 10), 0, true));
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   protected void onClientSetDead() {
      for(int i = 0; i < 20; ++i) {
         double width = 0.4D;
         double xPos = 0.3D + super.rand.nextDouble() * 0.4D;
         double zPos = 0.3D + super.rand.nextDouble() * 0.4D;
         double d0 = super.posX;
         double d1 = super.posY;
         double d2 = super.posZ;
         NaturePowerFX sparkle = new NaturePowerFX(super.worldObj, d0, d1, d2);
         sparkle.setScale(1.0F);
         sparkle.setGravity(0.2F);
         sparkle.setCanMove(true);
         sparkle.noClip = true;
         double maxSpeed = 0.08D;
         double doubleSpeed = 0.16D;
         sparkle.setVelocity(super.rand.nextDouble() * 0.16D - 0.08D, super.rand.nextDouble() * 0.05D + 0.12D, super.rand.nextDouble() * 0.16D - 0.08D);
         sparkle.setMaxAge(25 + super.rand.nextInt(10));
         float red = 1.0F;
         float green = 1.0F;
         float blue = 0.0F;
         float maxColorShift = 0.2F;
         float doubleColorShift = maxColorShift * 2.0F;
         float colorshiftR = super.rand.nextFloat() * doubleColorShift - maxColorShift;
         float colorshiftG = super.rand.nextFloat() * doubleColorShift - maxColorShift;
         float colorshiftB = super.rand.nextFloat() * doubleColorShift - maxColorShift;
         sparkle.setRBGColorF(red + colorshiftR, green + colorshiftG, blue + colorshiftB);
         sparkle.setAlphaF(0.1F);
         Minecraft.getMinecraft().effectRenderer.addEffect(sparkle);
      }

   }

   public void readEntityFromNBT(NBTTagCompound nbtRoot) {
      this.readEntityFromNBT(nbtRoot);
      this.setImpact(nbtRoot.getBoolean("Impacted"));
      this.blockPlaced = nbtRoot.getBoolean("BlockPlaced");
      if(this.blockPlaced) {
         this.blockX = nbtRoot.getInteger("BlockPlacedX");
         this.blockY = nbtRoot.getInteger("BlockPlacedY");
         this.blockZ = nbtRoot.getInteger("BlockPlacedZ");
      }

   }

   public void writeEntityToNBT(NBTTagCompound nbtRoot) {
      this.writeEntityToNBT(nbtRoot);
      nbtRoot.setBoolean("Impacted", this.getImpact());
      if(this.blockPlaced) {
         nbtRoot.setBoolean("BlockPlaced", this.blockPlaced);
         nbtRoot.setInteger("BlockPlacedX", this.blockX);
         nbtRoot.setInteger("BlockPlacedY", this.blockY);
         nbtRoot.setInteger("BlockPlacedZ", this.blockZ);
      }

   }
}
