import React, {Component} from 'react';
import PropTypes from 'prop-types';

class CardItem extends Component {

  getStyle = () => {
    return {
      float: 'left',
      position: 'relative',
      alignSelf: 'center',
      padding: '5px',
      textAlign: 'center'

    }
  }

  btnStyle = () => {
    return{
      border:'none',
      //outline:'none'
    }
  }


  render() {
    const{id,title,image} = this.props.cards;

    return (
      //<div style ={ {textAlign:"center"} } >
      <div style ={ this.getStyle() } >
        <button style = {this.btnStyle()}>
          <img className = "image"
            src={" "+image }
            alt={title}
            height="150"
            width="100"
            onClick={this.props.pressCard.bind(this,id)}
          />
        </button>
      </div>
      //</div>

    )
  }
}




CardItem.propTypes = {
  cards: PropTypes.object.isRequired
}


export default CardItem;
