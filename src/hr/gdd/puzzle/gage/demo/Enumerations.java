package hr.gdd.puzzle.gage.demo;

//-----------------Enumeration for the different orientations
enum Orientation
{
	Portrait,
	Landscape,
	IPortrait,
	ILandscape
}

//-----------------Enumeration for swipe directions
enum SwipeDirection
{
	Left,
	Right
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
enum AlienType
{
	Alex,
	Daisy,
	Brain,
	Crazy,
	Rocky
}