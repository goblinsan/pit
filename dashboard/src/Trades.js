import React from "react";
import {Table} from "react-bootstrap";

class TradeRow extends React.Component {
    render() {
        const trade = this.props.trade;
        return (
            <tr>
                <td>{trade.requester.name}</td>
                <td>{trade.owner.name}</td>
                <td>{trade.amount}</td>
            </tr>
        );
    }
}

class TradeTable extends React.Component {
    render() {
        const rows = [];

        this.props.trades.forEach((trade, index) => {
            rows.push(
                <TradeRow
                    trade={trade}/>
            );
        });

        return (
            <Table>
                <thead>
                <tr>
                    <th>Requester</th>
                    <th>Owner</th>
                    <th>Amount</th>
                </tr>
                </thead>
                <tbody>{rows}</tbody>
            </Table>
        );
    }
}

export default TradeTable;