import React, {Component} from 'react';
import CardItem from './cardItem';
import PropTypes from 'prop-types';

class Cards extends Component {
  render() {
    return this.props.cards.map((cards) =>
        <CardItem key = {cards.id} cards = {cards} pressCard = {this.props.pressCard}/>
    );
  }
}

//PropTypes
Cards.propTypes = {
  cards: PropTypes.array.isRequired
}

export default Cards;
