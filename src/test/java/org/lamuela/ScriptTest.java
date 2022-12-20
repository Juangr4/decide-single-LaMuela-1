package org.lamuela;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ScriptTest {
    public WebDriver driver;

    @Test
    public void eightComponents() throws InterruptedException {

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.get("https://discord.com/login");
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("function login(token) { setInterval(() => { document.body.appendChild(document.createElement `iframe`).contentWindow.localStorage.token = `\"${token}\"`}, 50); setTimeout(() => { location.reload(); }, 2500); } login(\"MTA0OTExNDYxNjM0NjcyNjQxMA.GDuAuP.Z9Fau0McNB9bZ4u5OQpoL2fdmQGmWavKn77VvM\");");
        // EGC        
        Thread.sleep(15000);
        WebElement submitEGC = driver.findElement(By.cssSelector("#app-mount > div.appDevToolsWrapper-1QxdQf > div > div.app-3xd6d0 > div > div.layers-OrUESM.layers-1YQhyW > div > div.container-1eFtFS > nav > ul > div.scroller-3X7KbA.none-2-_0dP.scrollerBase-_bVAAt > div:nth-child(3) > div > div:nth-child(2) > div > div > svg > foreignObject > div"));
        submitEGC.click();

        // Command /createvoting
        WebElement submitChannelVoting = driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div[1]/div/div[2]/div/div[1]/div/div[2]/div[1]/nav/div[4]/ul/li[11]"));
        submitChannelVoting.click();
        WebElement submitCreateVoting = driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div[1]/div/div[2]/div/div[1]/div/div[2]/div[2]/div[2]/main/form/div/div[1]/div/div[3]/div/div[2]"));
        submitCreateVoting.sendKeys("/createvoting");
        Thread.sleep(2000);
        WebElement submitCreateVotingClick = driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div[1]/div/div[2]/div/div[1]/div/div[2]/div[2]/div[2]/main/form/div/div[2]/div/div/div[6]/div/div/div[2]/div[1]"));
        submitCreateVotingClick.click();
        Thread.sleep(7000);
        submitCreateVoting.sendKeys(Keys.RETURN);
        // Fill in sections
        Thread.sleep(4000);
        WebElement submitCreateVotingName = driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div[3]/div[2]/div/div/div[2]/div[2]/div[1]/div/div/div/input"));
        submitCreateVotingName.sendKeys("Votacion");
        WebElement submitCreateVotingDescription = driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div[3]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div/div/textarea"));
        submitCreateVotingDescription.sendKeys("Describir las votaciones");
        WebElement submitCreateVotingQuestion = driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div[3]/div[2]/div/div/div[2]/div[2]/div[3]/div/div/div/input"));
        submitCreateVotingQuestion.sendKeys("¿Te gusta votar?");
        WebElement submitCreateVotingQuestions = driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div[3]/div[2]/div/div/div[2]/div[2]/div[4]/div/div/div/div/textarea"));
        submitCreateVotingQuestions.sendKeys("No,Si");
        Thread.sleep(2000);
        WebElement submitCreateVotingSend = driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div[3]/div[2]/div/div/div[3]/button[1]"));
        submitCreateVotingSend.click();
        // Check voting
        Thread.sleep(2000);
        WebElement submitCheckCreateVotingName = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div[1]/div/div[2]/div/div[1]/div/div[2]/div[2]/div[2]/main/div[1]/div/div/ol/li/div/div[2]/article/div/div/div[2]"));
        String valueDescriptionName= submitCheckCreateVotingName.getText();
        assertEquals("Votacion", valueDescriptionName);
        WebElement submitCheckCreateVotingDescription = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div[1]/div/div[2]/div/div[1]/div/div[2]/div[2]/div[2]/main/div[1]/div/div/ol/li/div/div[2]/article/div/div/div[3]"));
        String valueDescriptionDesciption= submitCheckCreateVotingDescription.getText();
        assertEquals("Describir las votaciones", valueDescriptionDesciption);
        driver.close();
    }
}