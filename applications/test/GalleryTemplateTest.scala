
package test

import conf.switches.Switches.FacebookShareUseTrailPicFirstSwitch
import org.scalatest.{DoNotDiscover, Matchers, FlatSpec}
import scala.collection.JavaConversions._

@DoNotDiscover class GalleryTemplateTest extends FlatSpec with Matchers with ConfiguredTestSuite {

  it should "render gallery headline" in goTo("/news/gallery/2012/may/02/picture-desk-live-kabul-burma") { browser =>
    browser.$("h1").first.getText should be("Picture desk live: the day's best news images")
  }

  it should "render captions" in goTo("/news/gallery/2012/may/02/picture-desk-live-kabul-burma") { browser =>
    import browser._
    $("div.gallery__caption").getTexts.firstNonEmpty.get should include("A TV grab from state-owned French television station France 2 showing the debate between Francois Hollande and Nicolas Sarkozy for the 2012 French presidential election campaign")
  }

  it should "render all images in the gallery" in goTo("/news/gallery/2012/may/02/picture-desk-live-kabul-burma") { browser =>
    import browser._
    $(".gallery__item:not(.gallery__item--advert)").length should be (22)
  }

  it should "insert ad slots after every four images" in goTo("/news/gallery/2012/may/02/picture-desk-live-kabul-burma") { browser =>
    import browser._
    $(".gallery__item--advert").length should be (5)
  }

  it should "show the twitter card meta-data" in goTo("/music/gallery/2012/jun/23/simon-bolivar-orchestra-dudamel-southbank-centre") { browser =>
    import browser._
    $("meta[name='twitter:card']").getAttributes("content").head should be ("summary_large_image")
    $("meta[name='twitter:title']").getAttributes("content").head should be ("Southbank Centre's Sounds Venezuela festival - in pictures")
  }

  it should "select the trail picture for the opengraph image when FacebookShareUseTrailPicFirstSwitch is ON" in {
    FacebookShareUseTrailPicFirstSwitch.switchOn()
    goTo("/lifeandstyle/gallery/2014/nov/24/flying-dogs-in-pictures") { browser =>
      import browser._
      $("meta[property='og:image']").getAttributes("content").head should include ("61e027cb-fec8-4aa3-a12b-e50f99493399-2060x1236.jpeg")
    }
  }

  it should "select the largest main picture for the opengraph image when FacebookShareUseTrailPicFirstSwitch is OFF" in {
    FacebookShareUseTrailPicFirstSwitch.switchOff()
    goTo("/lifeandstyle/gallery/2014/nov/24/flying-dogs-in-pictures") { browser =>
      import browser._
      $("meta[property='og:image']").getAttributes("content").head should include ("e3867edb-e9d5-4be9-9c51-12258b686869-1498x2040.jpeg")
    }
  }

  it should "include the index parameter in direct links" in goTo("/music/gallery/2012/jun/23/simon-bolivar-orchestra-dudamel-southbank-centre?index=2") { browser =>
    browser.findFirst("link[rel='canonical']").getAttribute("href") should endWith("/music/gallery/2012/jun/23/simon-bolivar-orchestra-dudamel-southbank-centre?index=2")
  }

  it should "render link to gallery most view onward journey page" in goTo("/news/gallery/2012/may/02/picture-desk-live-kabul-burma") { browser =>
    import browser._
    $(".most-viewed-no-js").first.getText should be("More galleries")
  }
}
