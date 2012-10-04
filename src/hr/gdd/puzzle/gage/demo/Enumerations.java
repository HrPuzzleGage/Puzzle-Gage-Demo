package hr.gdd.puzzle.gage.demo;

//-----------------Enumeration for the different orientations
enum Orientation
{
	Portrait,
	Landscape,
	IPortrait,
	ILandscape
}

//-----------------Enumeration for menu labels
enum DisplayLabel
{
	None,
	Score,
	Combo, 
	Time,
	Moves
}

//-----------------Enumeration for swipe directions
enum SwipeDirection
{
	Left,
	Right,
	None
}

//-----------------Enumeration for level phases
enum LevelPhase 
{
	Setup, 
	Playing, 
	Checking, 
	Paused
}

//-----------------Enumeration for alien types
enum BlockType
{
	Alex,
	Daisy,
	Brain,
	Crazy,
	Rocky
}

//-----------------Enumeration for event types
enum EventType
{
	DoneChecking,
	OrientationChanged,
	OrientationFound,
	TouchDown,
	TouchSwiped,
	TouchUp,
	ScoreUpdate
}

//-----------------Enumeration for blockfield switch states
enum SwitchState
{
	None,
	Switching,
	ISwitching
}