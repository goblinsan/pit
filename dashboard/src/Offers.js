import React from "react";
import {Table} from "react-bootstrap";

class OfferRow extends React.Component {
    render() {
        const offer = this.props.offer;
        return (
            <tr>
                <td>{offer.player.name}</td>
                <td>{offer.amount}</td>
            </tr>
        );
    }
}

class OfferTable extends React.Component {
    render() {
        const rows = [];

        this.props.offers.forEach((offer) => {
            rows.push(
                <OfferRow
                    offer={offer}
                    key={offer.player.name}/>
            );
        });

        return (
            <Table>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Amount</th>
                </tr>
                </thead>
                <tbody>{rows}</tbody>
            </Table>
        );
    }
}

export default OfferTable;