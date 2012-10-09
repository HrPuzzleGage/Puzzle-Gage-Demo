package hr.gdd.puzzle.gage.demo;

public class AlienBlockFactory implements IBlockFactory 
{
	@Override
	public Block createBlock(BlockConfig config) 
	{
		return new AlienBlock(config);
	}
}
