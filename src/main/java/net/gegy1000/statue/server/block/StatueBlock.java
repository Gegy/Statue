package net.gegy1000.statue.server.block;

import net.gegy1000.statue.Statue;
import net.gegy1000.statue.server.api.DefaultRenderedItem;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StatueBlock extends BlockContainer implements DefaultRenderedItem {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public StatueBlock() {
        super(Material.GLASS);
        this.setHardness(1.0F);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setSoundType(SoundType.WOOD);
    }

    @Override
    public IBlockState getStateFromMeta(int data) {
        EnumFacing face = EnumFacing.getFront(data);
        if (face.getAxis() == EnumFacing.Axis.Y) {
            face = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty(FACING, face);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rotation) {
        return state.withProperty(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation(state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof StatueBlockEntity) {
            StatueBlockEntity statue = (StatueBlockEntity) tile;
            statue.setOwner(placer.getUniqueID());
            if (placer instanceof EntityPlayerMP) {
                statue.watchChunk((EntityPlayerMP) placer);
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new StatueBlockEntity();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            this.openGUI(world, pos);
        }
        return true;
    }

    private void openGUI(World world, BlockPos pos) {
        Statue.PROXY.selectModel(world, pos);
    }
}
