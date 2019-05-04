import React, {Component} from 'react';
import Cards from './components/Cards';
import './App.css';
import startImage from './components/cardPics/card_back_alt.png'
import Header from './components/layout/Header.js'

class App extends Component {

  state = {
      cards: [
        {
          id: 1,
          title: 'Card 1',
          image: startImage,
          played: false
        },
        {
          id: 2,
          title: 'Card 2',
          image: startImage,
          played: false
        },
        {
          id: 3,
          title: 'Card 3',
          image: startImage,
          played: false
        },
        {
          id: 4,
          title: 'Card 4',
          image: startImage,
          played: false
        },
        {
          id: 5,
          title: 'Card 5',
          image: startImage,
          played: false
        },
        {
          id: 6,
          title: 'Card 6',
          image: startImage,
          played: false
        },
        {
          id: 7,
          title: 'Card 7',
          image: startImage,
          played: false
        }
      ]
  }

  //disable card played
  pressCard = (id) =>{
    console.log("Played "+id );
    this.setState( {cards: this.state.cards.map(cards =>{
      if(cards.id === id){
          cards.played = !cards.played
      }
      return cards;
    }) }) ;
  }

  render() {
    console.log(this.state.cards)
    return (
        <div className="App">
            <Header />
            <Cards cards ={this.state.cards} pressCard = {this.pressCard} />
        </div>
    );
  }

}

export default App;
