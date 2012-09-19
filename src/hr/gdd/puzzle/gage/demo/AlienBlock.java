package hr.gdd.puzzle.gage.demo;

public class AlienBlock extends Block 
{
	//Fields
	private String _type;
	
	//Constructor
	public AlienBlock(BlockConfig config)
	{
		this.setPosition(config.getPosition());
		this._type = config.getType();
	}
	
	//Implemented abstract methods
	@Override
	public String getType() {
		return this._type;
	}
}
