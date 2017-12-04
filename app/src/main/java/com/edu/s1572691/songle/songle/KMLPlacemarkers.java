package com.edu.s1572691.songle.songle;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rusab Asher on 28/11/2017.
 */
 public class KMLPlacemarkers
    {
        private Kml kml;

        public Kml getKml() { return this.kml; }

        public void setKml(Kml kml) { this.kml = kml; }
    }
    class Icon
    {
        private String href;

        public String getHref() { return this.href; }

        public void setHref(String href) { this.href = href; }
    }

    class IconStyle
    {
        private String scale;

        public String getScale() { return this.scale; }

        public void setScale(String scale) { this.scale = scale; }

        private Icon Icon;

        public Icon getIcon() { return this.Icon; }

        public void setIcon(Icon Icon) { this.Icon = Icon; }
    }

    /*class Style
    {
        private String id;

        public String getId() { return this.id; }

        public void setId(String id) { this.id = id; }

        private IconStyle IconStyle;

        public IconStyle getIconStyle() { return this.IconStyle; }

        public void setIconStyle(IconStyle IconStyle) { this.IconStyle = IconStyle; }
    }*/

    class Point
    {
        private String coordinates;

        public String getCoordinates() { return this.coordinates; }

        public void setCoordinates(String coordinates) { this.coordinates = coordinates; }
    }

    class Placemark
    {
        private String name;

        public String getName() { return this.name; }

        public void setName(String name) { this.name = name; }

        private String description;

        public String getDescription() { return this.description; }

        public void setDescription(String description) { this.description = description; }

        private String styleUrl;

        public String getStyleUrl() { return this.styleUrl; }

        public void setStyleUrl(String styleUrl) { this.styleUrl = styleUrl; }

        private Point Point;

        public Point getPoint() { return this.Point; }

        public void setPoint(Point Point) { this.Point = Point; }
    }

    class Document
    {
        //private Style[] Style;

        //public Style[] getStyle() { return this.Style; }

        //public void setStyle(Style[] Style) { this.Style = Style; }

        private ArrayList<Placemark> Placemark;

        public ArrayList<Placemark> getPlacemark() { return this.Placemark; }

        public void setPlacemark(ArrayList<Placemark> Placemark) { this.Placemark = Placemark; }
    }

    class Kml
    {
        private String xmlns;

        public String getXmlns() { return this.xmlns; }

        public void setXmlns(String xmlns) { this.xmlns = xmlns; }

        private Document Document;

        public Document getDocument() { return this.Document; }

        public void setDocument(Document Document) { this.Document = Document; }
    }

