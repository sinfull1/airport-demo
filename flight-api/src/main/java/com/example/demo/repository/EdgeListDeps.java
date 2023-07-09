package com.example.demo.repository;


import com.example.demo.graph.CustomNode;
import lombok.Data;


public interface EdgeListDeps {
     String getOrigin();
     String getOriginCity();

     String getDestination();

     String getDestCity();

     Object[] getAirline();
     Object[] getArrTimes();
     Object[] getDepTimes();

     long getFlightDate();

     default CustomNode getOriginNode() {
          return new CustomNode(this.getOrigin(), this.getOriginCity());
     }

     default CustomNode getDestNode() {
          return new CustomNode(this.getDestination(), this.getDestCity());
     }

}
