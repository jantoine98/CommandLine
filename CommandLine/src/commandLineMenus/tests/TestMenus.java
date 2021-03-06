package commandLineMenus.tests;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Stack;

import org.junit.Test;

import commandLineMenus.Menu;
import commandLineMenus.Menu.CycleDetectedException;
import commandLineMenus.Menu.EmptyMenuException;
import commandLineMenus.Option;

public class TestMenus
{
	
	private void compareOutputs(Stack<PrintedMenu> expected, MenuMockRenderer mock)
	{
		assertEquals(expected, mock.getPrintedMenus());
	}

	@Test
	public void checkTitle()
	{
		Menu menu = new Menu("Bonjour");
		assertEquals(menu.getTitle(), "Bonjour");
	}
	
	@Test
	public void checkShorcut()
	{
		Option option = new Option("Coucou", "sh");
		assertEquals(option.getTitle(), "Coucou");
		assertEquals(option.getShortcut(), "sh");
	}
	
	@Test
	public void addOption()
	{
		Menu menu = new Menu("Coucou");
		Option option = new Option("Option", "shorcut");
		menu.add(option);
		assertTrue(menu.getOptions().contains(option));
		assertEquals(menu.getOptions().size(), 1);
	}
	
	@Test
	public void quit()
	{
		Menu menu = new Menu("titre");
		MenuMockRenderer mock = new MenuMockRenderer(); 
		menu.setRenderer(mock);
		menu.addQuit("q");
		mock.input("q");
		menu.start();
		PrintedMenu output = new PrintedMenu();
		output.setTitle("titre");
		output.option("q", "Exit");
		Stack<PrintedMenu> outputs = new Stack<>();
		outputs.push(output);
		compareOutputs(outputs, mock);
	}
	
	@Test
	public void nestingOptions()
	{
		MenuMockRenderer mock = new MenuMockRenderer(); 
		mock.input("o");
		mock.input("o");
		mock.input("q");
		
		Menu menu = new Menu("Titre");
		menu.setRenderer(mock);
		
		Option option = new Option("Option", "o", () -> {});
		menu.add(option);
		
		menu.addQuit("q");
		menu.start();

		Stack<PrintedMenu> outputs = new Stack<>();
		PrintedMenu output = new PrintedMenu();

		output.setTitle("Titre");
		output.option("o", "Option");
		output.option("q", "Exit");
		
		outputs.push(output);
		outputs.push(output);
		outputs.push(output);

		compareOutputs(outputs, mock);
	}

	@Test
	public void checkRenderers()
	{
		MenuMockRenderer mock = new MenuMockRenderer(); 

		Menu menu = new Menu("Titre");
		menu.setRenderer(mock);
		
		Option option = new Option("Option", "o", () -> {});
		menu.add(option);
		menu.addQuit("q");
		
		mock.input("q");
		menu.start();
		
		assertEquals(menu.getRenderer(), mock);
		assertEquals(option.getRenderer(), mock);
	}
	
	@Test
	public void checkCycle()
	{
		Menu racine = new Menu("racine", "r"),
			feuille = new Menu("feuille", "r");
		racine.add(feuille);
		feuille.add(racine);
		try
		{
			racine.start();
			assertTrue("An exception should have occurred !", false);
		}
		catch(CycleDetectedException c)		
		{
			List<Menu> cycle = c.getCycleDetected();
			assertEquals(cycle.get(0), racine);
			assertEquals(cycle.get(1), feuille);
			assertEquals(cycle.get(2), racine);
		}
	}

	@Test
	public void checkEmptyMnu()
	{
		Menu racine = new Menu("racine", "r");
		try
		{
			racine.start();
			assertTrue("An exception should have occurred !", false);
		}
		catch(EmptyMenuException e)		
		{
			assertEquals(e.getMenu(), racine);
		}
	}
}
